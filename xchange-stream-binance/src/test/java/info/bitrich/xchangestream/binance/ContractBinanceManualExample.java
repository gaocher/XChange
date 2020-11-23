package info.bitrich.xchangestream.binance;

import static info.bitrich.xchangestream.binance.BinanceStreamingExchange.USE_HIGHER_UPDATE_FREQUENCY;

import info.bitrich.xchangestream.core.ProductSubscription;
import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import io.reactivex.disposables.Disposable;
import java.io.IOException;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.ContractCurrencyPair;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.ContractBalanceInfo;
import org.knowm.xchange.dto.account.ContractPositionInfo;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Created by Lukas Zaoralek on 15.11.17. */
public class ContractBinanceManualExample {
  private static final Logger LOG = LoggerFactory.getLogger(ContractBinanceManualExample.class);
  //PERP
  static ContractCurrencyPair BTCUSD_PERP = new ContractCurrencyPair(Currency.BTC, Currency.USD, "PERP");

  static ContractCurrencyPair BTCUSD_201225 = new ContractCurrencyPair(Currency.BTC, Currency.USD, "201225");

  public static void main(String[] args) throws InterruptedException, IOException {
    // Far safer than temporarily adding these to code that might get committed to VCS
    String apiKey = System.getProperty("binance-api-key");
    String apiSecret = System.getProperty("binance-api-secret");

    apiKey = "9cxwWaYAEcvVRqYrLoCNKwjBgB2ipzPUk0rrJmgDJeaIJhLVnz9p37ljWoeAQVNb";
    apiSecret = "acJsbXrAcGhhhQbuSkbCdWwjz1SEWZN3LrK8EznWZtrWseX00EZ7D70VCueP2DGG";

//    apiKey = "Mpdsdb8BxDgW17AhqderPtykHJyGRnyRzKJpbkRqxgyNVAqOHsiHvVDhFYRCmatv";
//    apiSecret = "EdP5cyOmWn5jKONuL8TVVizm9WUJfqDAAKK8bTdaj5sBXFnUDg3Flc8NBd7LicTL";
    ExchangeSpecification spec =
        StreamingExchangeFactory.INSTANCE
            .createExchange(ContractBinanceStreamingExchange.class.getName())
            .getDefaultExchangeSpecification();
    spec.setExchangeSpecificParametersItem(USE_HIGHER_UPDATE_FREQUENCY, true);
    spec.setApiKey(apiKey);
    spec.setSecretKey(apiSecret);
    BinanceStreamingExchange exchange =
        (BinanceStreamingExchange) StreamingExchangeFactory.INSTANCE.createExchange(spec);

//    ContractCurrencyPair currencyPair = BTCUSD_PERP;
    ContractCurrencyPair currencyPair = BTCUSD_201225;

    ProductSubscription subscription =
        ProductSubscription.create()
//            .addTicker(CurrencyPair.ETH_BTC)
//            .addTicker(CurrencyPair.LTC_BTC)
//            .addOrderbook(currencyPair)
//            .addTrades(currencyPair)
//            .addMarkPrice(currencyPair)
            .addOrders(currencyPair)
            .build();

    long start = System.currentTimeMillis();
    OrderBook orderBook = exchange.getMarketDataService().getOrderBook(BTCUSD_201225);
    LOG.info("orderbook {} {}ms", orderBook, System.currentTimeMillis() - start);

     start = System.currentTimeMillis();
     orderBook = exchange.getMarketDataService().getOrderBook(BTCUSD_201225);
    LOG.info("orderbook {} {}ms", orderBook, System.currentTimeMillis() - start);




    ContractBalanceInfo balanceInfo = exchange.getAccountService().getBalanceInfo();
    ContractPositionInfo contractPositionInfo = exchange.getAccountService().getContractPositionInfo();

//    LimitOrder build = new Builder(OrderType.BID, BTCUSD_201225)
//        .limitPrice(BigDecimal.valueOf(10400))
//        .originalAmount(BigDecimal.ONE).build();

//    try {
//      start = System.currentTimeMillis();
//      build.getOrderFlags().add(TimeInForce.FOK);
//      String orderId = exchange.getTradeService().placeLimitOrder(build);
//
//      LOG.info("order id {} {}ms", orderId, System.currentTimeMillis() - start);
//
//      start = System.currentTimeMillis();
//      DefaultQueryOrderParamCurrencyPair defaultQueryOrderParamCurrencyPair = new DefaultQueryOrderParamCurrencyPair(
//          BTCUSD_201225, orderId);
//      Collection<Order> order = exchange.getTradeService().getOrder(defaultQueryOrderParamCurrencyPair);
//      for (Order o : order) {
//        LOG.info("order {} {}ms", o, System.currentTimeMillis() - start);
//      }
//      BinanceCancelOrderParams binanceCancelOrderParams = new BinanceCancelOrderParams(
//          BTCUSD_201225, orderId);
//      start = System.currentTimeMillis();
//      boolean b = exchange.getTradeService().cancelOrder(binanceCancelOrderParams);
//      LOG.info("canncel order id {} is {} {}ms",orderId, b, System.currentTimeMillis() - start);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }

    exchange.connect(subscription).blockingAwait();
    LOG.info("Subscribing public channels");

    exchange.getStreamingTradeService().getOrderChanges().subscribe(order -> {
      LOG.info("order is {}", order);
    });

//    Disposable tickers =
//        exchange
//            .getStreamingMarketDataService()
//            .getTicker(CurrencyPair.ETH_BTC)
//            .subscribe(
//                ticker -> {
//                  LOG.info("Ticker: {}", ticker);
//                },
//                throwable -> LOG.error("ERROR in getting ticker: ", throwable));

//    Disposable trades =
//        exchange
//            .getStreamingMarketDataService()
//            .getTrades(currencyPair)
//            .subscribe(
//                trade -> {
//                  LOG.info("Trade: {}", trade);
//                });
//
//    exchange.getStreamingMarketDataService().getMarkPrice(currencyPair)
//        .subscribe(binanceMarkPrice -> {
//          LOG.info("binanceMarkPrice: {}", binanceMarkPrice);
//        });

    Disposable orderChanges = null;
    Disposable userTrades = null;
    Disposable balances = null;
    Disposable accountInfo = null;
    Disposable executionReports = null;
//
//    if (apiKey != null) {
//
//      LOG.info("Subscribing authenticated channels");
//
//      // Level 1 (generic) APIs
//      orderChanges =
//          exchange
//              .getStreamingTradeService()
//              .getOrderChanges()
//              .subscribe(oc -> LOG.info("Order change: {}", oc));
//      userTrades =
//          exchange
//              .getStreamingTradeService()
//              .getUserTrades()
//              .subscribe(trade -> LOG.info("User trade: {}", trade));
//      balances =
//          exchange
//              .getStreamingAccountService()
//              .getBalanceChanges()
//              .subscribe(
//                  trade -> LOG.info("Balance: {}", trade),
//                  e -> LOG.error("Error in balance stream", e));
//
//      // Level 2 (exchange-specific) APIs
//      executionReports =
//          exchange
//              .getStreamingTradeService()
//              .getRawExecutionReports()
//              .subscribe(report -> LOG.info("Subscriber got execution report: {}", report));
//      accountInfo =
//          exchange
//              .getStreamingAccountService()
//              .getRawAccountInfo()
//              .subscribe(
//                  accInfo ->
//                      LOG.info(
//                          "Subscriber got account Info (not printing, often causes console issues in IDEs)"));
//    }
//

//    Disposable orderbooks = orderbooks(currencyPair, exchange, "one");
//    Thread.sleep(5000);
//    Disposable orderbooks2 = orderbooks(exchange, "two");
//
//    Thread.sleep(1000000);

//    tickers.dispose();
//    trades.dispose();
//    orderbooks.dispose();
//    orderbooks2.dispose();

//    if (apiKey != null) {
//      orderChanges.dispose();
//      userTrades.dispose();
//      balances.dispose();
//      accountInfo.dispose();
//      executionReports.dispose();
//    }

//    exchange.disconnect().blockingAwait();
  }

  private static Disposable orderbooks(CurrencyPair currencyPair, StreamingExchange exchange, String identifier) {
    return exchange
        .getStreamingMarketDataService()
        .getOrderBook(currencyPair)
        .subscribe(
            orderBook -> {
              LOG.info(
                  "Order Book {}ms({}): askDepth={} ask={} askSize={} bidDepth={}. bid={}, bidSize={}",
                  System.currentTimeMillis() - orderBook.getTimeStamp().getTime(),
                  identifier,
                  orderBook.getAsks().size(),
                  orderBook.getAsks().get(0).getLimitPrice(),
                  orderBook.getAsks().get(0).getRemainingAmount(),
                  orderBook.getBids().size(),
                  orderBook.getBids().get(0).getLimitPrice(),
                  orderBook.getBids().get(0).getRemainingAmount());
            },
            throwable -> LOG.error("ERROR in getting order book: ", throwable));
  }
}
