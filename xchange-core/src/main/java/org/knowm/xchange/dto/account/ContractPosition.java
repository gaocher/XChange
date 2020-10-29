package org.knowm.xchange.dto.account;

import java.math.BigDecimal;
import org.knowm.xchange.currency.CurrencyPair;

/**
 * @author Isaac Gao
 * @Date 2020/10/29
 */
public class ContractPosition {
  private CurrencyPair currencyPair;
  private BigDecimal amount;
  private BigDecimal entryPrice;
  private BigDecimal markPrice;
  private BigDecimal unrealizedPnl;
  private BigDecimal liquidationPrice;
  private int leverage;
  private int maxQty;
  private MarginType marginType;
  private PositionSide positionSide;

  public ContractPosition(CurrencyPair symbol, BigDecimal amount, BigDecimal entryPrice,
      BigDecimal markPrice, BigDecimal unrealizedPnl, BigDecimal liquidationPrice, int leverage,
      int maxQty, MarginType marginType, PositionSide positionSide) {
    this.currencyPair = symbol;
    this.amount = amount;
    this.entryPrice = entryPrice;
    this.markPrice = markPrice;
    this.unrealizedPnl = unrealizedPnl;
    this.liquidationPrice = liquidationPrice;
    this.leverage = leverage;
    this.maxQty = maxQty;
    this.marginType = marginType;
    this.positionSide = positionSide;
  }

  public CurrencyPair getCurrencyPair() {
    return currencyPair;
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

  public PositionSide getPositionSide() {
    return positionSide;
  }
}
