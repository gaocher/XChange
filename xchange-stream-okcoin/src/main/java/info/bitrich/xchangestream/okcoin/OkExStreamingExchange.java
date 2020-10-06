package info.bitrich.xchangestream.okcoin;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import io.reactivex.Completable;
import io.reactivex.Observable;
import org.knowm.xchange.okcoin.OkCoinExchange;
import org.knowm.xchange.okcoin.OkexExchangeV3;

public class OkExStreamingExchange extends OkexExchangeV3 implements StreamingExchange {
  private static final String API_URI = "wss://real.okex.com:8443/ws/v3";

  private final OkExStreamingService streamingService;
  private OkExStreamingMarketDataService streamingMarketDataService;

  public OkExStreamingExchange() {
    streamingService = new OkExStreamingService(API_URI);
  }

  public OkExStreamingExchange(String apiUrl) {
    streamingService = new OkExStreamingService(apiUrl);
  }

  protected OkExStreamingExchange(OkExStreamingService streamingService) {
    this.streamingService = streamingService;
  }

  @Override
  protected void initServices() {
    super.initServices();
    streamingMarketDataService = new OkExStreamingMarketDataService(streamingService);
  }

  @Override
  public Completable connect(ProductSubscription... args) {
    return streamingService.connect();
  }

  @Override
  public Completable disconnect() {
    return streamingService.disconnect();
  }

  @Override
  public boolean isAlive() {
    return streamingService.isSocketOpen();
  }

  @Override
  public Observable<Throwable> reconnectFailure() {
    return streamingService.subscribeReconnectFailure();
  }

  @Override
  public Observable<Object> connectionSuccess() {
    return streamingService.subscribeConnectionSuccess();
  }

  @Override
  public StreamingMarketDataService getStreamingMarketDataService() {
    return streamingMarketDataService;
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }
}
