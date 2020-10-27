package org.knowm.xchange.binance.dto.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import org.knowm.xchange.currency.Currency;

/**
 * @author Isaac Gao
 * @Date 2020/10/26
 */
public class BinanceContractBalance {
  private final Currency currency;
  private final BigDecimal balance;
  private final BigDecimal withdrawAvailable;
  private final BigDecimal crossWalletBalance;
  private final BigDecimal crossUnPnl;
  private final BigDecimal availableBalance;
  private final Date timestamp;

  public BinanceContractBalance(
      @JsonProperty("asset") String currency,
      @JsonProperty("balance")BigDecimal balance,
      @JsonProperty("withdrawAvailable")BigDecimal withdrawAvailable,
      @JsonProperty("crossWalletBalance")BigDecimal crossWalletBalance,
      @JsonProperty("crossUnPnl")BigDecimal crossUnPnl,
      @JsonProperty("availableBalance")BigDecimal availableBalance,
      @JsonProperty("updateTime") long timestamp) {
    this.currency = Currency.getInstance(currency);
    this.balance = balance;
    this.withdrawAvailable = withdrawAvailable;
    this.crossWalletBalance = crossWalletBalance;
    this.crossUnPnl = crossUnPnl;
    this.availableBalance = availableBalance;
    this.timestamp = new Date(timestamp);
  }

  public Currency getCurrency() {
    return currency;
  }

  public BigDecimal getBalance() {
    return balance;
  }

  public BigDecimal getWithdrawAvailable() {
    return withdrawAvailable;
  }

  public BigDecimal getCrossWalletBalance() {
    return crossWalletBalance;
  }

  public BigDecimal getCrossUnPnl() {
    return crossUnPnl;
  }

  public BigDecimal getAvailableBalance() {
    return availableBalance;
  }

  public Date getTimestamp() {
    return timestamp;
  }
}
