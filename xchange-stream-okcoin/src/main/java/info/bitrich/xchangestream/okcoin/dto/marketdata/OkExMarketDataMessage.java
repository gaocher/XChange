package info.bitrich.xchangestream.okcoin.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import lombok.Data;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trade.Builder;
import org.knowm.xchange.dto.trade.LimitOrder;

/**
 * @author Isaac Gao
 * @Date 2020/7/13
 */
@Data
public class OkExMarketDataMessage {
  private String table;
  private String event;
  private String channel;
  private String message;
  private String errorCode;
  private List<DataNode> data = new ArrayList<>();

  public static ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @JsonIgnore
  public String getChannelName() {
    if (data.size() > 0) {
      return table + ":" + data.get(0).instrument_id;
    }
    return table;
  }

  public static OkExMarketDataMessage of(String s) {
    try {
      return objectMapper.readValue(s, OkExMarketDataMessage.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }


  @Data
  public static class DataNode {
    private String action;
    private String instrument_id;
    private String price;
    private String side;
    private String size;
    private String timestamp;
    private String trade_id;
    private List<BigDecimal[]> asks = new ArrayList<>();
    private List<BigDecimal[]> bids = new ArrayList<>();

    public Trade toTrade(CurrencyPair cp) {
      return new Trade.Builder()
          .type(this.getSide().equals("buy") ? OrderType.BID : OrderType.ASK)
          .originalAmount(new BigDecimal(this.getSize()))
          .currencyPair(cp)
          .price(new BigDecimal(this.getPrice()))
          .timestamp(getDate(this.getTimestamp()))
          .id(this.getTrade_id())
          .build();
    }

    public Date getTimeStampDate() {
      return getDate(this.getTimestamp());
    }

    private static Date getDate(String exchangeTime) {
      Instant parse = Instant.parse(exchangeTime);
      return Date.from(parse);
    }

    public OrderBook toOrderBook() {
      List<LimitOrder> askList = asks.stream()
          .map(a -> new LimitOrder(OrderType.ASK, a[1], toCurrentPair(), null, getTimeStampDate(),
              a[0]))
          .collect(Collectors.toList());
      List<LimitOrder> bidList = bids.stream()
          .map(a -> new LimitOrder(OrderType.BID, a[1], toCurrentPair(), null, getTimeStampDate(),
              a[0]))
          .collect(Collectors.toList());
      return new OrderBook(getTimeStampDate(), askList, bidList);

    }
    public CurrencyPair toCurrentPair() {
      String[] split = instrument_id.split("-");
      return new CurrencyPair(split[0], split[1]);
    }
  }

  @Data
  public static class OrderBookItem {
    private BigDecimal price;
    private BigDecimal amount;
    private int count;
  }
}
