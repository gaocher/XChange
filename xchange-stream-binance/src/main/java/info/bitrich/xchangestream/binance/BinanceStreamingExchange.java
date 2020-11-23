package info.bitrich.xchangestream.binance;

import com.google.common.base.MoreObjects;
import info.bitrich.xchangestream.binance.BinanceUserDataChannel.NoActiveChannelException;
import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.util.Events;
import io.reactivex.Completable;
import io.reactivex.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.knowm.xchange.binance.BinanceExchange;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BinanceStreamingExchange extends BinanceExchange implements StreamingExchange {

  private static final Logger LOG = LoggerFactory.getLogger(BinanceStreamingExchange.class);
  public  static final String USE_HIGHER_UPDATE_FREQUENCY =
      "Binance_Orderbook_Use_Higher_Frequency";

  protected BinanceStreamingService streamingService;
  protected BinanceUserDataStreamingService userDataStreamingService;

  protected BinanceStreamingMarketDataService streamingMarketDataService;
  protected BinanceStreamingAccountService streamingAccountService;
  protected BinanceStreamingTradeService streamingTradeService;

  private BinanceUserDataChannel userDataChannel;
  protected Runnable onApiCall;
  protected String orderBookUpdateFrequencyParameter = "";

  protected String getStreamingApiBaseUri() {
    return "wss://stream.binance.com:9443/";
  }

  protected String getUserStreamingApiBaseUri() {
    return getStreamingApiBaseUri() + "ws/";
  }

  @Override
  protected void initServices() {
    super.initServices();
    this.onApiCall = Events.onApiCall(exchangeSpecification);
    Boolean userHigherFrequency =
        MoreObjects.firstNonNull(
            (Boolean)
                exchangeSpecification.getExchangeSpecificParametersItem(
                    USE_HIGHER_UPDATE_FREQUENCY),
            Boolean.FALSE);

    if (userHigherFrequency) {
      orderBookUpdateFrequencyParameter = "@100ms";
    }
  }

  /**
   * Binance streaming API expects connections to multiple channels to be defined at connection
   * time. To define the channels for this connection pass a `ProductSubscription` in at connection
   * time.
   *
   * @param args A single `ProductSubscription` to define the subscriptions required to be available
   *     during this connection.
   * @return A completable which fulfils once connection is complete.
   */
  @Override
  public Completable connect(ProductSubscription... args) {
    if (args == null || args.length == 0) {
      throw new IllegalArgumentException("Subscriptions must be made at connection time");
    }
    if (streamingService != null) {
      throw new UnsupportedOperationException(
          "Exchange only handles a single connection - disconnect the current connection.");
    }

    ProductSubscription subscriptions = args[0];
    streamingService = createStreamingService(subscriptions);

    List<Completable> completables = new ArrayList<>();

    if (subscriptions.hasUnauthenticated()) {
      completables.add(streamingService.connect());
    }

    if (subscriptions.hasAuthenticated()) {
      if (exchangeSpecification.getApiKey() == null) {
        throw new IllegalArgumentException("API key required for authenticated streams");
      }

      LOG.info("Connecting to authenticated web socket");
      userDataChannel =
          new BinanceUserDataChannel(binance, exchangeSpecification.getApiKey(), onApiCall);
      try {
        completables.add(createAndConnectUserDataService(userDataChannel.getListenKey()));
      } catch (NoActiveChannelException e) {
        throw new IllegalStateException("Failed to establish user data channel", e);
      }
    }

    initConcreteStreamService();

    return Completable.concat(completables)
        .doOnComplete(() -> streamingMarketDataService.openSubscriptions(subscriptions))
        .doOnComplete(() -> streamingAccountService.openSubscriptions())
        .doOnComplete(() -> streamingTradeService.openSubscriptions());
  }

  protected void initConcreteStreamService() {
    streamingMarketDataService =
        new BinanceStreamingMarketDataService(
            streamingService,
            (BinanceMarketDataService) marketDataService,
            onApiCall,
            orderBookUpdateFrequencyParameter);
    streamingAccountService = new BinanceStreamingAccountService(userDataStreamingService);
    streamingTradeService = new SpotBinanceStreamingTradeService(userDataStreamingService);
  }

  private Completable createAndConnectUserDataService(String listenKey) {
    String userDataUrl = getUserStreamingApiBaseUri() + listenKey;
    userDataStreamingService = BinanceUserDataStreamingService.create(userDataUrl);
    return userDataStreamingService
        .connect()
        .doOnComplete(
            () -> {
              LOG.info("Connected to authenticated web socket");
              userDataChannel.onChangeListenKey(
                  newListenKey -> {
                    userDataStreamingService
                        .disconnect()
                        .doOnComplete(
                            () -> {
                              createAndConnectUserDataService(newListenKey)
                                  .doOnComplete(
                                      () -> {
                                        streamingAccountService.setUserDataStreamingService(
                                            userDataStreamingService);
                                        streamingTradeService.setUserDataStreamingService(
                                            userDataStreamingService);
                                      });
                            });
                  });
            });
  }

  @Override
  public Completable disconnect() {
    List<Completable> completables = new ArrayList<>();
    completables.add(streamingService.disconnect());
    streamingService = null;
    if (userDataStreamingService != null) {
      completables.add(userDataStreamingService.disconnect());
      userDataStreamingService = null;
    }
    if (userDataChannel != null) {
      userDataChannel.close();
      userDataChannel = null;
    }
    streamingMarketDataService = null;
    return Completable.concat(completables);
  }

  @Override
  public boolean isAlive() {
    return streamingService != null && streamingService.isSocketOpen();
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
  public BinanceStreamingAccountService getStreamingAccountService() {
    return streamingAccountService;
  }

  @Override
  public BinanceStreamingTradeService getStreamingTradeService() {
    return streamingTradeService;
  }

  protected BinanceStreamingService createStreamingService(ProductSubscription subscription) {
    String path = getStreamingApiBaseUri() + "stream?streams=" + buildSubscriptionStreams(subscription);
    return new BinanceStreamingService(path, subscription);
  }

  public String buildSubscriptionStreams(ProductSubscription subscription) {
    return Stream.of(
            buildSubscriptionStrings(subscription.getTicker(), "ticker"),
            buildSubscriptionStrings(subscription.getOrderBook(), "depth"),
            buildSubscriptionStrings(subscription.getTrades(), "trade"),
            buildSubscriptionStrings(subscription.getMarkPrice(), "markPrice"))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.joining("/"));
  }

  private String buildSubscriptionStrings(
      List<CurrencyPair> currencyPairs, String subscriptionType) {

    if ("depth".equals(subscriptionType)) {
      return subscriptionStrings(currencyPairs)
          .map(s -> s + "@" + subscriptionType + orderBookUpdateFrequencyParameter)
          .collect(Collectors.joining("/"));
    } else {
      return subscriptionStrings(currencyPairs)
          .map(s -> s + "@" + subscriptionType)
          .collect(Collectors.joining("/"));
    }
  }

  private static Stream<String> subscriptionStrings(List<CurrencyPair> currencyPairs) {
    return currencyPairs.stream()
        .map(pair -> String.join("", pair.toString().split("/")).toLowerCase());
  }

  @Override
  public void useCompressedMessages(boolean compressedMessages) {
    streamingService.useCompressedMessages(compressedMessages);
  }
}
