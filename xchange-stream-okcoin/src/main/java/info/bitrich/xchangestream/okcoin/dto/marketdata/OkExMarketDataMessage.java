package info.bitrich.xchangestream.okcoin.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.Data;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order.OrderType;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trade.Builder;

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
    private String instrument_id;
    private String price;
    private String side;
    private String size;
    private String timestamp;
    private String trade_id;

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

    private static Date getDate(String exchangeTime) {
      Instant parse = Instant.parse(exchangeTime);
      return Date.from(parse);
    }
  }
}
