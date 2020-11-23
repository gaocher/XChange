package info.bitrich.xchangestream.binance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.bitrich.xchangestream.binance.dto.BaseBinanceWebSocketTransaction.BinanceWebSocketTypes;
import info.bitrich.xchangestream.binance.dto.OrderUpdateBinanceUserTransaction;
import info.bitrich.xchangestream.service.netty.StreamingObjectMapperHelper;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.io.IOException;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.ExchangeSecurityException;

/**
 * @author Isaac Gao
 * @Date 2020/11/23
 */
public class ContractBinanceStreamingTradeService implements BinanceStreamingTradeService {

  private final Subject<OrderUpdateBinanceUserTransaction> orderUpdateBinancePublisher =
      PublishSubject.<OrderUpdateBinanceUserTransaction>create().toSerialized();

  private volatile Disposable executionReports;
  private volatile BinanceUserDataStreamingService binanceUserDataStreamingService;

  private final ObjectMapper mapper = StreamingObjectMapperHelper.getObjectMapper();

  public ContractBinanceStreamingTradeService(
      BinanceUserDataStreamingService binanceUserDataStreamingService) {
    this.binanceUserDataStreamingService = binanceUserDataStreamingService;
  }

  public Observable<OrderUpdateBinanceUserTransaction> getRawExecutionReports() {
    if (binanceUserDataStreamingService == null || !binanceUserDataStreamingService.isSocketOpen())
      throw new ExchangeSecurityException("Not authenticated");
    return orderUpdateBinancePublisher;
  }

  public Observable<Order> getOrderChanges() {
    return getRawExecutionReports()
        .map(OrderUpdateBinanceUserTransaction::toOrder);
  }

  @Override
  public Observable<UserTrade> getUserTrades() {
    return null;
    //    return getRawExecutionReports()
//        .filter(r -> r.getExecutionType().equals(ExecutionType.TRADE))
//        .map(ExecutionReportBinanceUserTransaction::toUserTrade);
  }

  @Override
  public Observable<Order> getOrderChanges(CurrencyPair currencyPair, Object... args) {
    if (currencyPair == null) {
      return getOrderChanges();
    }
    return getOrderChanges().filter(oc -> currencyPair.equals(oc.getInstrument()));
  }

  @Override
  public Observable<UserTrade> getUserTrades(CurrencyPair currencyPair, Object... args) {
    throw new IllegalArgumentException("unsupported!");
//    return getUserTrades().filter(t -> t.getCurrencyPair().equals(currencyPair));
  }

  /** Registers subsriptions with the streaming service for the given products. */
  @Override
  public void openSubscriptions() {
    if (binanceUserDataStreamingService != null) {
      executionReports =
          binanceUserDataStreamingService
              .subscribeChannel(BinanceWebSocketTypes.ORDER_TRADE_UPDATE)
              .map(this::parseOrderUpdate)
              .subscribe(orderUpdateBinancePublisher::onNext);
    }
  }

  /**
   * User data subscriptions may have to persist across multiple socket connections to different
   * URLs and therefore must act in a publisher fashion so that subscribers get an uninterrupted
   * stream.
   */
  @Override
  public void setUserDataStreamingService(
      BinanceUserDataStreamingService binanceUserDataStreamingService) {
    if (executionReports != null && !executionReports.isDisposed()) executionReports.dispose();
    this.binanceUserDataStreamingService = binanceUserDataStreamingService;
    openSubscriptions();
  }

  private OrderUpdateBinanceUserTransaction parseOrderUpdate(JsonNode json) {
    try {
      return mapper.treeToValue(json, OrderUpdateBinanceUserTransaction.class);
    } catch (IOException e) {
      throw new ExchangeException("Unable to parse execution report", e);
    }
  }

}
