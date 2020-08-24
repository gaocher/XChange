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
  private String contractNo;

  public ChannelSubscriptionMessage(CurrencyPair currencyPair, String tradeType,
      String dataType, String contractNo) {
    this.currencyPair = currencyPair;
    this.tradeType = tradeType;
    this.dataType = dataType;
    this.contractNo = contractNo;
  }

  public ChannelSubscriptionMessage(CurrencyPair currencyPair, String tradeType,
      String dataType) {
    this(currencyPair, tradeType, dataType, null);
  }

  public String getFullChannelName() {
    String s = tradeType + "/" + dataType + ":" + currencyPair.base.toString().toUpperCase() + "-"
        + currencyPair.counter.toString().toUpperCase();
    if (tradeType.equals("swap")) {
      return s + "-SWAP";
    } else if (tradeType.equals("futures")) {
      return s + "-" + contractNo;
    }
    return s;
  }
}
