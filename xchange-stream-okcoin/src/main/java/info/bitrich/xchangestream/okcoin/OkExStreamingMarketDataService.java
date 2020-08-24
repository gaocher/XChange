package info.bitrich.xchangestream.okcoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.okcoin.dto.ChannelSubscriptionMessage;
import info.bitrich.xchangestream.okcoin.dto.OkCoinOrderbook;
import info.bitrich.xchangestream.okcoin.dto.OkCoinWebSocketTrade;
import info.bitrich.xchangestream.okcoin.dto.marketdata.OkExMarketDataMessage.DataNode;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.OrderBookUpdate;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.okcoin.OkCoinAdapters;
import org.knowm.xchange.okcoin.dto.marketdata.OkCoinDepth;

/**
 * @author Isaac Gao
 * @Date 2020/7/13
 */
@Slf4j
public class OkExStreamingMarketDataService implements StreamingMarketDataService {

  private final OkExStreamingService service;
  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();
  private final Map<String, OrderBook> orderbooks = new HashMap<>();

  public OkExStreamingMarketDataService(
      OkExStreamingService service) {
    this.service = service;
  }

  @Override
  public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
    if (! (args[0] instanceof ChannelSubscriptionMessage)) {
      throw new IllegalArgumentException("error in subscription");
    }
    ChannelSubscriptionMessage subscriptionMessage = (ChannelSubscriptionMessage) args[0];
    String fullChannelName = subscriptionMessage.getFullChannelName();
    String key = fullChannelName;
    return service
        .subscribeChannel(fullChannelName, subscriptionMessage)
        .flatMap( s -> Observable.fromIterable(s.getData()))
        .map(
            s -> {
              OrderBook orderBook;
              if (!orderbooks.containsKey(key)) {
                orderBook = s.toOrderBook();
                orderbooks.put(key, orderBook);
              } else {
                orderBook = orderbooks.get(key);
                for (BigDecimal[] ask : s.getAsks()) {
                  OrderBookUpdate orderBookUpdate = new OrderBookUpdate(
                      OrderType.ASK,
                      null,
                      currencyPair,
                      ask[0],
                      s.getTimeStampDate(),
                      ask[1]);
                  orderBook.update(orderBookUpdate);
                }
                for (BigDecimal[] bid : s.getBids()) {
                  OrderBookUpdate orderBookUpdate = new OrderBookUpdate(
                      OrderType.BID,
                      null,
                      currencyPair,
                      bid[0],
                      s.getTimeStampDate(),
                      bid[1]);
                  orderBook.update(orderBookUpdate);
                }
              }
              return orderBook;
            });
  }

  @Override
  public Observable<Ticker> getTicker(CurrencyPair currencyPair, Object... args) {
    return null;
  }

  @Override
  public Observable<Trade> getTrades(CurrencyPair currencyPair, Object... args) {
    if (! (args[0] instanceof ChannelSubscriptionMessage)) {
      throw new IllegalArgumentException("error in subscription");
    }
    ChannelSubscriptionMessage subscriptionMessage = (ChannelSubscriptionMessage) args[0];
    return service
        .subscribeChannel(subscriptionMessage.getFullChannelName(), subscriptionMessage)
        .map(
            m -> {
              return m.getData().stream().map(s -> s.toTrade(currencyPair)).collect(Collectors.toList());
            })
        .flatMapIterable(s -> s);
  }
}
