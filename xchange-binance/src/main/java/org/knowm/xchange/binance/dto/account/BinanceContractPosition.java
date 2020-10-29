package org.knowm.xchange.binance.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import org.knowm.xchange.binance.BinanceAdapters;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.account.MarginType;
import org.knowm.xchange.dto.account.PositionSide;

/**
 * @author Isaac Gao
 * @Date 2020/10/29
 */
public class BinanceContractPosition {
  private CurrencyPair symbol;
  private BigDecimal amount;
  private BigDecimal entryPrice;
  private BigDecimal markPrice;
  private BigDecimal unrealizedPnl;
  private BigDecimal liquidationPrice;
  private int leverage;
  private int maxQty;
  private MarginType marginType;
  private BigDecimal isolatedMargin;
  private boolean isAutoAddMargin;
  private PositionSide positionSide;

  public BinanceContractPosition(
      @JsonProperty("symbol") String symbol,
      @JsonProperty("positionAmt")BigDecimal amount,
      @JsonProperty("entryPrice")BigDecimal entryPrice,
      @JsonProperty("markPrice") BigDecimal markPrice,
      @JsonProperty("unRealizedProfit")BigDecimal unrealizedPnl,
      @JsonProperty("liquidationPrice")BigDecimal liquidationPrice,
      @JsonProperty("leverage")int leverage,
      @JsonProperty("maxQty")int maxQty,
      @JsonProperty("marginType")String marginType,
      @JsonProperty("isolatedMargin")BigDecimal isolatedMargin,
      @JsonProperty("isAutoAddMargin")boolean isAutoAddMargin,
      @JsonProperty("positionSide")String positionSide) {
    this.symbol = BinanceAdapters.adaptSymbol(symbol);
    this.positionSide = amount.compareTo(BigDecimal.ZERO) > 0 ? PositionSide.LONG : PositionSide.SHORT;
    this.amount = amount.abs();
    this.entryPrice = entryPrice;
    this.markPrice = markPrice;
    this.unrealizedPnl = unrealizedPnl;
    this.liquidationPrice = liquidationPrice;
    this.leverage = leverage;
    this.maxQty = maxQty;
    this.marginType = "cross".equals(marginType) ? MarginType.CROSSED : MarginType.ISOLATED;
    this.isolatedMargin = isolatedMargin;
    this.isAutoAddMargin = isAutoAddMargin;
  }

  public CurrencyPair getSymbol() {
    return symbol;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public BigDecimal getEntryPrice() {
    return entryPrice;
  }

  public BigDecimal getMarkPrice() {
    return markPrice;
  }

  public BigDecimal getUnrealizedPnl() {
    return unrealizedPnl;
  }

  public BigDecimal getLiquidationPrice() {
    return liquidationPrice;
  }

  public int getLeverage() {
    return leverage;
  }

  public int getMaxQty() {
    return maxQty;
  }

  public MarginType getMarginType() {
    return marginType;
  }

  public BigDecimal getIsolatedMargin() {
    return isolatedMargin;
  }

  public boolean isAutoAddMargin() {
    return isAutoAddMargin;
  }

  public PositionSide getPositionSide() {
    return positionSide;
  }
}
