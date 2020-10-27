package org.knowm.xchange.dto.account;

import java.math.BigDecimal;
import java.util.Date;
import org.knowm.xchange.currency.Currency;

/**
 * @author Isaac Gao
 * @Date 2020/10/26
 */
public class ContractBalance {
  private final Currency currency;
  private final BigDecimal balance;
  private final BigDecimal withdrawAvailable;
  private final BigDecimal crossWalletBalance;
  private final BigDecimal crossUnPnl;
  private final BigDecimal availableBalance;
  private final Date timestamp;

  public ContractBalance(Currency currency, BigDecimal balance,
      BigDecimal withdrawAvailable, BigDecimal crossWalletBalance, BigDecimal crossUnPnl,
      BigDecimal availableBalance, Date timestamp) {
    this.currency = currency;
    this.balance = balance;
    this.withdrawAvailable = withdrawAvailable;
    this.crossWalletBalance = crossWalletBalance;
    this.crossUnPnl = crossUnPnl;
    this.availableBalance = availableBalance;
    this.timestamp = timestamp;
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
