package info.bitrich.xchangestream.okcoin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import info.bitrich.xchangestream.okcoin.dto.OkExSocketMessage;
import info.bitrich.xchangestream.okcoin.dto.marketdata.OkExMarketDataMessage;
import info.bitrich.xchangestream.service.netty.NettyStreamingService;
import info.bitrich.xchangestream.service.netty.WebSocketClientHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;
import org.knowm.xchange.exceptions.ExchangeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OkExStreamingService extends NettyStreamingService<OkExMarketDataMessage> {
  private static final Logger LOG = LoggerFactory.getLogger(OkExStreamingService.class);

  private Observable<Long> pingPongSrc = Observable.interval(15, 15, TimeUnit.SECONDS);

  private Disposable pingPongSubscription;

  public OkExStreamingService(String apiUrl) {
    super(apiUrl);
  }

  @Override
  protected String getChannelNameFromMessage(OkExMarketDataMessage message) throws IOException {
    return message.getChannelName();
  }

  @Override
  public Completable connect() {
    Completable conn = super.connect();
    return conn.andThen(
        (CompletableSource)
            (completable) -> {
              try {
//                if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
//                  pingPongSubscription.dispose();
//                }
//                pingPongSubscription =
//                    pingPongSrc.subscribe(
//                        o -> {
//                          this.sendMessage("ping");
//                        });
                completable.onComplete();
              } catch (Exception e) {
                completable.onError(e);
              }
            });
  }

  @Override
  public String getSubscribeMessage(String channelName, Object... args) throws IOException {
    OkExSocketMessage webSocketMessage = new OkExSocketMessage("subscribe", channelName);
    return webSocketMessage.getFullString();
  }

  @Override
  public String getUnsubscribeMessage(String channelName) throws IOException {
    OkExSocketMessage webSocketMessage = new OkExSocketMessage("unsubscribe", channelName);
    return webSocketMessage.getFullString();
  }

  @Override
  public void messageHandler(String message) {
    LOG.debug("received " + message);
    if ("pong".equals(message)) {
      LOG.info("received message: " + message);
      return;
    }
    handleMessage(OkExMarketDataMessage.of(message));
  }

  @Override
  protected void handleMessage(OkExMarketDataMessage message) {
    if (message.getEvent() != null) {
      // ignore pong message
      LOG.info("receive event response {}", message);
      return;
    }

    super.handleMessage(message);
  }

  @Override
  protected WebSocketClientHandler getWebSocketClientHandler(
      WebSocketClientHandshaker handshaker,
      WebSocketClientHandler.WebSocketMessageHandler handler) {
    return new OkCoinNettyWebSocketClientHandler(handshaker, handler);
  }

  protected class OkCoinNettyWebSocketClientHandler extends NettyWebSocketClientHandler {

    private final Logger LOG = LoggerFactory.getLogger(OkCoinNettyWebSocketClientHandler.class);

    protected OkCoinNettyWebSocketClientHandler(
        WebSocketClientHandshaker handshaker, WebSocketMessageHandler handler) {
      super(handshaker, handler);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
      if (pingPongSubscription != null && !pingPongSubscription.isDisposed()) {
        pingPongSubscription.dispose();
      }
      super.channelInactive(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

      if (!handshaker.isHandshakeComplete()) {
        super.channelRead0(ctx, msg);
        return;
      }

      super.channelRead0(ctx, msg);

      WebSocketFrame frame = (WebSocketFrame) msg;
      if (frame instanceof BinaryWebSocketFrame) {
        BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
        ByteBuf byteBuf = binaryFrame.content();
        byte[] temp = new byte[byteBuf.readableBytes()];
        ByteBufInputStream bis = new ByteBufInputStream(byteBuf);
        StringBuilder appender = new StringBuilder();
        try {
          bis.read(temp);
          bis.close();
          Inflater infl = new Inflater(true);
          infl.setInput(temp, 0, temp.length);
          byte[] result = new byte[1024];
          while (!infl.finished()) {
            int length = infl.inflate(result);
            appender.append(new String(result, 0, length, "UTF-8"));
          }
          infl.end();
        } catch (Exception e) {
          LOG.trace("Error when inflate websocket binary message");
        }
        handler.onMessage(appender.toString());
      }
    }
  }
}
