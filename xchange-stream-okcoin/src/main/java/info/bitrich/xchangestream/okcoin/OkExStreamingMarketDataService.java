package info.bitrich.xchangestream.okcoin;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.core.StreamingMarketDataService;
import info.bitrich.xchangestream.okcoin.dto.ChannelSubscriptionMessage;
import info.bitrich.xchangestream.okcoin.dto.OkCoinWebSocketTrade;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import java.util.stream.Collectors;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.okcoin.OkCoinAdapters;

/**
 * @author Isaac Gao
 * @Date 2020/7/13
 */
public class OkExStreamingMarketDataService implements StreamingMarketDataService {

  private final OkExStreamingService service;
  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  public OkExStreamingMarketDataService(
      OkExStreamingService service) {
    this.service = service;
  }

  @Override
  public Observable<OrderBook> getOrderBook(CurrencyPair currencyPair, Object... args) {
//    if (! (args[0] instanceof ChannelSubscriptionMessage)) {
//      throw new IllegalArgumentException("error in subscription");
//    }
//    ChannelSubscriptionMessage subscriptionMessage = (ChannelSubscriptionMessage) args[0];
//    return service
//        .subscribeChannel(subscriptionMessage.getFullChannelName(), subscriptionMessage)
//        .map(
//            m -> {
//              return m.getData().stream().map(s -> s.toTrade(currencyPair)).collect(Collectors.toList());
//            })
//        .flatMapIterable(s -> s);
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
