package info.bitrich.xchangestream.okcoin;

public class OkExFuturesStreamingExchange extends OkExStreamingExchange {

//  private static final String API_URI = "wss://real.okex.com:10440/websocket/okexapi?compress=true";

  private static final String API_URI = "wss://real.okex.com:8443/ws/v3";


  public OkExFuturesStreamingExchange() {
    super(API_URI);
  }
}
