package info.bitrich.xchangestream.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.Data;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.binance.dto.trade.BinanceOrder;
import org.knowm.xchange.binance.dto.trade.OrderSide;
import org.knowm.xchange.binance.dto.trade.OrderStatus;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.TimeInForce;
import org.knowm.xchange.dto.account.PositionType;

/**
 * @author Isaac Gao
 * @Date 2020/11/23
 */
public class OrderUpdateBinanceUserTransaction extends BaseBinanceWebSocketTransaction {

  public enum ExecutionType {
    NEW,
    CANCELED,
    REPLACED,
    REJECTED,
    TRADE,
    EXPIRED
  }

  private String accountAlias;

  private long timestamp;

  private OrderUpdateMessage orderUpdate;

  public OrderUpdateBinanceUserTransaction(
      @JsonProperty("e") String eventType,
      @JsonProperty("E") String eventTime,
      @JsonProperty("i") String accountAlias,
      @JsonProperty("T") long timestamp,
      @JsonProperty("o") OrderUpdateMessage orderUpdate) {
    super(eventType, eventTime);
    this.accountAlias = accountAlias;
    this.timestamp = timestamp;
    this.orderUpdate = orderUpdate;
  }

  public Order toOrder() {
    return BinanceAdapters.adaptOrder(
        new BinanceOrder(
            BinanceAdapters.toSymbol(orderUpdate.getCurrencyPair()),
            orderUpdate.getOrderId(),
            orderUpdate.getClientOrderId(),
            orderUpdate.getOriginalPrice(),
            orderUpdate.getOriginalAmount(),
            orderUpdate.getLastExecutedQuantity(),
            orderUpdate.getCumulativeFilledQuantity(),
            orderUpdate.getCurrentOrderStatus(),
            orderUpdate.getTimeInForce(),
            orderUpdate.getOrderType(),
            orderUpdate.getOrderSide(),
            orderUpdate.getStopPrice(),
            BigDecimal.ZERO,
            timestamp,
            orderUpdate.getAveragePrice()));
  }



  @Data
  public static class OrderUpdateMessage {

    private final CurrencyPair currencyPair;
    private final String clientOrderId;
    private final org.knowm.xchange.binance.dto.trade.OrderType orderType;
    private final OrderSide orderSide;
    private final TimeInForce timeInForce;
    private final BigDecimal originalAmount;
    private final BigDecimal originalPrice;
    private final BigDecimal averagePrice;
    private final BigDecimal stopPrice;
    private final ExecutionType executionType;
    private final OrderStatus currentOrderStatus;
    private final long orderId;
    private final BigDecimal lastExecutedQuantity;
    private final BigDecimal cumulativeFilledQuantity;
    private final BigDecimal lastExecutedPrice;
    private final BigDecimal commissionAmount;
    private final String commissionAsset;
    private final long timestamp;
    private final long tradeId;
    private final boolean buyerMarketMaker;
    private final PositionType positionType;

    public OrderUpdateMessage(
        @JsonProperty("s") String symbol,  //:"BTCUSD_200925",                    // 交易对
        @JsonProperty("c") String clientOrderId, //:"TEST",                     // 客户端自定订单ID
             // 特殊的自定义订单ID:
             // "autoclose-"开头的字符串: 系统强平订单
             // "delivery-"开头的字符串: 系统交割平仓单
        @JsonProperty("S") OrderSide orderSide, //:"SELL",                     // 订单方向
        @JsonProperty("o") org.knowm.xchange.binance.dto.trade.OrderType orderType, //:"LIMIT",                    // 订单类型
        @JsonProperty("f") TimeInForce timeInForce, //"GTC",                      // 有效方式
        @JsonProperty("q") BigDecimal originalAmount, //:"0.001",                    // 订单原始数量
        @JsonProperty("p") BigDecimal originalPrice, //:"9910",                     // 订单原始价格
        @JsonProperty("ap") BigDecimal averagePrice, //:"0",                       // 订单平均价格
        @JsonProperty("sp") BigDecimal stopPrice, //:"0",                       // 订单停止价格
        @JsonProperty("x") String currentExecutionType,
        @JsonProperty("X") String currentOrderStatus, //"x":"NEW",                      // 本次事件的具体执行类型
              //"X":"NEW",                      // 订单的当前状态
        @JsonProperty("i") long orderId,
        @JsonProperty("l") BigDecimal lastExecutedQuantity,
        @JsonProperty("z") BigDecimal cumulativeFilledQuantity,
        @JsonProperty("L") BigDecimal lastExecutedPrice,
        @JsonProperty("n") BigDecimal commissionAmount,
        @JsonProperty("N") String commissionAsset,
        @JsonProperty("T") long timestamp,
        @JsonProperty("t") long tradeId,
        @JsonProperty("m") boolean buyerMarketMaker,
        @JsonProperty("ps") PositionType positionType

//        "i":8886774,                    // 订单ID
//             "l":"0",                        // 订单末次成交量
//             "z":"0",                        // 订单累计已成交量
//             "L":"0",                        // 订单末次成交价格
//             "ma": "BTC",                    // 保证金资产类型
//             "N": "BTC",                     // 该成交手续费资产类型
//             "n": "0",                       // 该成交手续费数量
//             "T":1568879465651,              // 成交时间
//             "t":0,                          // 成交ID
//             "rp": "0",                      // 该成交已实现盈亏
//             "b":"0",                        // 买单净值
//             "a":"9.91",                     // 卖单净值
//             "m": false,                     // 该成交是作为挂单成交吗？
//             "R":false   ,                   // 是否是只减仓单
//             "wt": "CONTRACT_PRICE",         // 触发价类型
//             "ot": "LIMIT",                  // 原始订单类型
//             "ps":"LONG",                        // 持仓方向
//             "cp":false,                     // 是否为触发平仓单
//             "AP":"7476.89",                 // 追踪止损激活价格
//             "cr":"5.0",                     // 追踪止损回调比例
//             "pP": false                     // 是否开启条件单触发保护

    ) {
      this.currencyPair = BinanceAdapters.convert(symbol);
      this.clientOrderId = clientOrderId;
      this.orderSide = orderSide;
      this.orderType = orderType;
      this.timeInForce = timeInForce;
      this.originalAmount = originalAmount;
      this.originalPrice = originalPrice;
      this.averagePrice = averagePrice;
      this.stopPrice = stopPrice;
      this.executionType = ExecutionType.valueOf(currentExecutionType);
      this.currentOrderStatus = OrderStatus.valueOf(currentOrderStatus);
      this.orderId = orderId;
      this.lastExecutedQuantity = lastExecutedQuantity;
      this.cumulativeFilledQuantity = cumulativeFilledQuantity;
      this.lastExecutedPrice = lastExecutedPrice;
      this.commissionAmount = commissionAmount;
      this.commissionAsset = commissionAsset;
      this.timestamp = timestamp;
      this.tradeId = tradeId;
      this.buyerMarketMaker = buyerMarketMaker;
      this.positionType = positionType;
    }
  }
}
