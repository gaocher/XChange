package info.bitrich.xchangestream.binance;

import info.bitrich.xchangestream.core.StreamingTradeService;
import io.reactivex.Observable;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.UserTrade;

/**
 * @author Isaac Gao
 * @Date 2020/11/23
 */
public interface BinanceStreamingTradeService extends StreamingTradeService {
  void openSubscriptions();

  void setUserDataStreamingService(BinanceUserDataStreamingService binanceUserDataStreamingService);

  Observable<Order> getOrderChanges();

  Observable<UserTrade> getUserTrades();
}
