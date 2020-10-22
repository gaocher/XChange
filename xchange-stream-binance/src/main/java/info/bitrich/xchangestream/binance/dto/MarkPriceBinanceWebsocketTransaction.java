package info.bitrich.xchangestream.binance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import org.knowm.xchange.binance.dto.marketdata.BinanceMarkPrice;

/**
 * @author Isaac Gao
 * @Date 2020/10/22
 */
public class MarkPriceBinanceWebsocketTransaction extends ProductBinanceWebSocketTransaction {

  private BinanceMarkPrice binanceMarkPrice;

  public MarkPriceBinanceWebsocketTransaction(
      @JsonProperty("e") String eventType,
      @JsonProperty("E") String eventTime,
      @JsonProperty("s") String symbol,
      @JsonProperty("p") BigDecimal price,
      @JsonProperty("P") BigDecimal predicatedSettlementPrice,
      @JsonProperty("r") BigDecimal assetRate,
      @JsonProperty("T") long nextAssetRateTime) {
    super(eventType, eventTime, symbol);
    this.binanceMarkPrice = new BinanceMarkPrice(eventType, eventTime, symbol, price, predicatedSettlementPrice, assetRate, nextAssetRateTime);
  }

  public BinanceMarkPrice getBinanceMarkPrice() {
    return binanceMarkPrice;
  }
}
