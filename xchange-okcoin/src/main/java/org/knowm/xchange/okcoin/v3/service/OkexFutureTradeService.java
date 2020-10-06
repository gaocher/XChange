package org.knowm.xchange.okcoin.v3.service;

import static org.knowm.xchange.okcoin.OkexAdaptersV3.toInstrument;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.TimeInForce;
import org.knowm.xchange.dto.marketdata.Trades.TradeSortType;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.dto.trade.MarketOrder;
import org.knowm.xchange.dto.trade.OpenOrders;
import org.knowm.xchange.dto.trade.UserTrade;
import org.knowm.xchange.dto.trade.UserTrades;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.okcoin.OkexAdaptersV3;
import org.knowm.xchange.okcoin.OkexExchangeV3;
import org.knowm.xchange.okcoin.v3.dto.trade.FuturesOrderPlacementRequest;
import org.knowm.xchange.okcoin.v3.dto.trade.FuturesSwapType;
import org.knowm.xchange.okcoin.v3.dto.trade.OkexFuturesOpenOrder;
import org.knowm.xchange.okcoin.v3.dto.trade.OkexOpenOrder;
import org.knowm.xchange.okcoin.v3.dto.trade.OkexOrderFlags;
import org.knowm.xchange.okcoin.v3.dto.trade.OkexTradeHistoryParams;
import org.knowm.xchange.okcoin.v3.dto.trade.OkexTransaction;
import org.knowm.xchange.okcoin.v3.dto.trade.OrderCancellationRequest;
import org.knowm.xchange.okcoin.v3.dto.trade.OrderCancellationResponse;
import org.knowm.xchange.okcoin.v3.dto.trade.OrderPlacementResponse;
import org.knowm.xchange.okcoin.v3.dto.trade.OrderPlacementType;
import org.knowm.xchange.okcoin.v3.dto.trade.Side;
import org.knowm.xchange.okcoin.v3.dto.trade.SpotOrderPlacementRequest;
import org.knowm.xchange.service.trade.TradeService;
import org.knowm.xchange.service.trade.params.CancelOrderByCurrencyPair;
import org.knowm.xchange.service.trade.params.CancelOrderByIdParams;
import org.knowm.xchange.service.trade.params.CancelOrderParams;
import org.knowm.xchange.service.trade.params.TradeHistoryParamCurrencyPair;
import org.knowm.xchange.service.trade.params.TradeHistoryParams;
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.DefaultQueryOrderParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParamCurrencyPair;
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams;
import org.knowm.xchange.service.trade.params.orders.OrderQueryParams;

/**
 * @author Isaac Gao
 * @Date 2020/10/6
 */
public class OkexFutureTradeService extends OkexTradeServiceRaw implements TradeService {
  private static final int orders_limit = 100;
  private static final int transactions_limit = 100;


  public OkexFutureTradeService(OkexExchangeV3 exchange) {
    super(exchange);
  }


  @Override
  public String placeLimitOrder(LimitOrder o) throws IOException {

    // 0: Normal limit order (Unfilled and 0 represent normal limit order) 1: Post only 2: Fill Or
    // Kill 3: Immediatel Or Cancel
    OrderPlacementType orderType =
        (o.hasFlag(OkexOrderFlags.POST_ONLY) || o.hasFlag(TimeInForce.GTX))
            ? OrderPlacementType.post_only
            : OrderPlacementType.normal;

    FuturesSwapType type = FuturesSwapType.to(o);
    FuturesOrderPlacementRequest req =
        FuturesOrderPlacementRequest.builder()
            .instrumentId(toInstrument(o.getInstrument()))
            .price(o.getLimitPrice())
            .size(o.getOriginalAmount())
            .type(type)
            .orderType(orderType)
            .build();
    OrderPlacementResponse placed = futuresPlaceOrder(req);
    return placed.getOrderId();
  }

  @Override
  public String placeMarketOrder(MarketOrder o) throws IOException {
    // 0: Normal limit order (Unfilled and 0 represent normal limit order) 1: Post only 2: Fill Or
    // Kill 3: Immediatel Or Cancel 4: market price
    //市价委托
    OrderPlacementType orderType = OrderPlacementType.market_price;

    FuturesSwapType type = FuturesSwapType.to(o);
    FuturesOrderPlacementRequest req =
        FuturesOrderPlacementRequest.builder()
            .instrumentId(toInstrument(o.getInstrument()))
            .size(o.getOriginalAmount())
            .type(type)
            .orderType(orderType)
            .build();
    OrderPlacementResponse placed = futuresPlaceOrder(req);
    return placed.getOrderId();
  }

  @Override
  public boolean cancelOrder(String orderId) throws IOException {
    throw new NotAvailableFromExchangeException();
  }

  @Override
  public boolean cancelOrder(CancelOrderParams orderParams) throws IOException {
    if (!(orderParams instanceof CancelOrderByIdParams)
        || !(orderParams instanceof CancelOrderByCurrencyPair)) {
      throw new UnsupportedOperationException(
          "Cancelling an order is only available for a single market and a single id.");
    }

    String orderId = ((CancelOrderByIdParams) orderParams).getOrderId();
    String instrumentId =
        toInstrument(
            ((CancelOrderByCurrencyPair) orderParams).getCurrencyPair());

    OrderCancellationResponse o = futuresCancelOrder(instrumentId, orderId);
    return true;
  }


  @Override
  public Collection<Order> getOrder(OrderQueryParams... orderQueryParams) throws IOException {
    if (orderQueryParams.length != 1) {
      throw new IllegalArgumentException("only support single query but size " + orderQueryParams.length);
    }

    OrderQueryParams orderQueryParam = orderQueryParams[0];
    if (orderQueryParam instanceof DefaultQueryOrderParamCurrencyPair) {
      String instrumentId = toInstrument(
          ((DefaultQueryOrderParamCurrencyPair) orderQueryParam).getCurrencyPair());
      OkexFuturesOpenOrder futuresOrderDetails = getFuturesOrderDetails(
          orderQueryParam.getOrderId(), instrumentId);

      LimitOrder convert = OkexAdaptersV3.convert(futuresOrderDetails);
      return Arrays.asList(convert);
    }
    throw new IllegalArgumentException("order query param is not qualified");
  }
}

