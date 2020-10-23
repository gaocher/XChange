package org.knowm.xchange.binance.dto.marketdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

/**
 * @author Isaac Gao
 * @Date 2020/10/22
 */
public class BinanceMarkPrice {
  private final String eventType;
  private final long eventTime;
  private final String symbol;
  private final BigDecimal price;
  private final BigDecimal predicatedSettlementPrice;
  private final BigDecimal assetRate;
  private final long nextAssetRateTime;

  public BinanceMarkPrice(String eventType, long eventTime, String symbol,
      BigDecimal price, BigDecimal predicatedSettlementPrice, BigDecimal assetRate,
      long nextAssetRateTime) {
    this.eventType = eventType;
    this.eventTime = eventTime;
    this.symbol = symbol;
    this.price = price;
    this.predicatedSettlementPrice = predicatedSettlementPrice;
    this.assetRate = assetRate;
    this.nextAssetRateTime = nextAssetRateTime;
  }

  public String getEventType() {
    return eventType;
  }

  public long getEventTime() {
    return eventTime;
  }

  public String getSymbol() {
    return symbol;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public BigDecimal getPredicatedSettlementPrice() {
    return predicatedSettlementPrice;
  }

  public BigDecimal getAssetRate() {
    return assetRate;
  }

  public long getNextAssetRateTime() {
    return nextAssetRateTime;
  }
}
