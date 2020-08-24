package info.bitrich.xchangestream.okcoin.dto;

import org.knowm.xchange.currency.CurrencyPair;

/**
 * @author Isaac Gao
 * @Date 2020/7/13
 */

public class ChannelSubscriptionMessage {
  private CurrencyPair currencyPair;
  private String tradeType; //spot, future, forever
  private String dataType; //ticker, candle

  public ChannelSubscriptionMessage(CurrencyPair currencyPair, String tradeType,
      String dataType) {
    this.currencyPair = currencyPair;
    this.tradeType = tradeType;
    this.dataType = dataType;
  }

  public String getFullChannelName() {
    return tradeType + "/" + dataType + ":" + currencyPair.base.toString().toUpperCase() + "-"
        + currencyPair.counter.toString().toUpperCase();
  }
}
