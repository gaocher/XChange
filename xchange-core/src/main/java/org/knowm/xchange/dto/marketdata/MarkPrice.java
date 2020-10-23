package org.knowm.xchange.dto.marketdata;

import java.math.BigDecimal;
import java.util.Date;
import org.knowm.xchange.currency.CurrencyPair;

/**
 * @author Isaac Gao
 * @Date 2020/10/22
 */

public class MarkPrice {
  private final CurrencyPair currencyPair;
  private final BigDecimal price;
  private final BigDecimal predicatedSettlementPrice;
  private final BigDecimal assetRate;
  private final long nextAssetRateTime;
  private final Date timestamp;

  public MarkPrice(CurrencyPair currencyPair, BigDecimal price,
      BigDecimal predicatedSettlementPrice, BigDecimal assetRate, long nextAssetRateTime,
      Date timestamp) {
    this.currencyPair = currencyPair;
    this.price = price;
    this.predicatedSettlementPrice = predicatedSettlementPrice;
    this.assetRate = assetRate;
    this.nextAssetRateTime = nextAssetRateTime;
    this.timestamp = timestamp;
  }

  public CurrencyPair getCurrencyPair() {
    return currencyPair;
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

  public Date getTimestamp() {
    return timestamp;
  }

  public long getCloseTimeToAssetRate() {
    return nextAssetRateTime - timestamp.getTime();
  }

  @Override
  public String toString() {
    return "MarkPrice{" +
        "currencyPair=" + currencyPair +
        ", price=" + price +
        ", predicatedSettlementPrice=" + predicatedSettlementPrice +
        ", assetRate=" + assetRate +
        ", nextAssetRateTime=" + nextAssetRateTime +
        ", timestamp=" + timestamp +
        '}';
  }
}
