package info.bitrich.xchangestream.okcoin;

import static org.knowm.xchange.dto.Order.PositionOrderFlags.CLOSE_POSITION;
import static org.knowm.xchange.dto.Order.PositionOrderFlags.OPEN_POSITION;
import static org.knowm.xchange.okcoin.OkexExchangeV3.USE_FUTURES_SPEC_ITEM;

import info.bitrich.xchangestream.core.StreamingExchange;
import info.bitrich.xchangestream.core.StreamingExchangeFactory;
import info.bitrich.xchangestream.okcoin.dto.ChannelSubscriptionMessage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.currency.ContractCurrencyPair;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.LimitOrder.Builder;
import org.knowm.xchange.okcoin.service.OkCoinTradeService.OkCoinCancelOrderParam;
import org.knowm.xchange.service.trade.params.CancelOrderByPairAndIdParams;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.DefaultCancelOrderByCurrencyPairAndIdParams;
import org.knowm.xchange.service.trade.params.orders.DefaultQueryOrderParamCurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
/** Created by Lukas Zaoralek on 17.11.17. */
public class OkExManualExample {
  private static final Logger LOG = LoggerFactory.getLogger(OkExManualExample.class);
  static ContractCurrencyPair BTCUSD_200925 = new ContractCurrencyPair(Currency.BTC, Currency.USD, "201225");

  public static void main(String[] args) {

    ExchangeSpecification spec =
        StreamingExchangeFactory.INSTANCE
            .createExchange(OkExStreamingExchange.class.getName())
            .getDefaultExchangeSpecification();
    spec.setExchangeSpecificParametersItem(USE_FUTURES_SPEC_ITEM, true);
    StreamingExchange exchange = StreamingExchangeFactory.INSTANCE.createExchange(spec);

//    exchange.connect().blockingAwait();


    LimitOrder build = new Builder(OrderType.BID, BTCUSD_200925)
        .limitPrice(BigDecimal.valueOf(10810))
        .originalAmount(BigDecimal.ONE).build();
    build.addOrderFlag(OPEN_POSITION);

    try {
      long start = System.currentTimeMillis();
//      build.getOrderFlags().add(TimeInForce.GTX);
      String orderId;
      try {
        orderId = exchange.getTradeService().placeLimitOrder(build);
      } catch (Exception e) {
        log.error("order exception", e);
        throw e;
      }

      LOG.info("order id {} {}ms", orderId, System.currentTimeMillis() - start);

      start = System.currentTimeMillis();
      DefaultQueryOrderParamCurrencyPair defaultQueryOrderParamCurrencyPair = new DefaultQueryOrderParamCurrencyPair(BTCUSD_200925, orderId);
      Collection<Order> order = exchange.getTradeService().getOrder(defaultQueryOrderParamCurrencyPair);
      for (Order o : order) {
        LOG.info("order {} {}ms", o, System.currentTimeMillis() - start);
      }
      DefaultCancelOrderByCurrencyPairAndIdParams binanceCancelOrderParams = new DefaultCancelOrderByCurrencyPairAndIdParams(
          BTCUSD_200925, orderId);
      start = System.currentTimeMillis();
      boolean b = exchange.getTradeService().cancelOrder(binanceCancelOrderParams);
      LOG.info("canncel order id {} is {} {}ms",orderId, b, System.currentTimeMillis() - start);
    } catch (IOException e) {
      e.printStackTrace();
    }


    CurrencyPair btcUsdt = CurrencyPair.BTC_USDT;
//    ChannelSubscriptionMessage channelForOrderBookMsg = new ChannelSubscriptionMessage(btcUsdt,
//        "swap", "depth");

    ChannelSubscriptionMessage channelForOrderBookMsg = new ChannelSubscriptionMessage(btcUsdt,
        "futures", "depth", "200925");
    exchange
        .getStreamingMarketDataService()
        .getOrderBook(btcUsdt, channelForOrderBookMsg)
        .subscribe(
            orderBook -> {
              LOG.info("First ask: {}", orderBook.getAsks().get(0));
              LOG.info("First bid: {}", orderBook.getBids().get(0));
            },
            throwable -> LOG.error("ERROR in getting order book: ", throwable));

//    exchange
//        .getStreamingMarketDataService()
//        .getTicker(btcUsdt)
//        .subscribe(
//            ticker -> {
//              LOG.info("TICKER: {}", ticker);
//            },
//            throwable -> LOG.error("ERROR in getting ticker: ", throwable));
    ChannelSubscriptionMessage channelSubscriptionMessage = new ChannelSubscriptionMessage(btcUsdt,
        "spot", "trade");
//    exchange
//        .getStreamingMarketDataService()
//        .getTrades(btcUsdt, channelSubscriptionMessage)
//        .subscribe(
//            trade -> {
//              LOG.info("TRADE: {}", trade);
//            },
//            throwable -> LOG.error("ERROR in getting trades: ", throwable));

    try {
      Thread.sleep(10000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
