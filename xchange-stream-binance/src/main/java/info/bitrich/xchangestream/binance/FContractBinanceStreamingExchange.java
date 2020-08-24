package info.bitrich.xchangestream.binance;

import info.bitrich.xchangestream.core.ProductSubscription;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.binance.Binance;
import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.BinanceTimestampFactory;
import org.knowm.xchange.binance.service.BinanceAccountService;
import org.knowm.xchange.binance.service.BinanceMarketDataService;
import org.knowm.xchange.binance.service.BinanceTradeService;
import org.knowm.xchange.client.ExchangeRestProxyBuilder;
import org.knowm.xchange.utils.AuthUtils;

/**
 * @author Isaac Gao
 * @Date 2020/7/13
 */
public class FContractBinanceStreamingExchange extends BinanceStreamingExchange {

  @Override
  protected BinanceStreamingService createStreamingService(ProductSubscription subscription) {
    String path =  "wss://fstream.binance.com/stream?streams=" + buildSubscriptionStreams(subscription);
    return new BinanceStreamingService(path, subscription);
  }

  public ExchangeSpecification getCustomizedExchangeSpecification() {

    ExchangeSpecification spec = new ExchangeSpecification(this.getClass().getCanonicalName());
    spec.setSslUri("https://fapi.binance.com");
    spec.setHost("www.binance.com");
    spec.setPort(80);
    spec.setExchangeName("Binance");
    spec.setExchangeDescription("Binance Exchange.");
    AuthUtils.setApiAndSecretKey(spec, "binance");
    return spec;
  }

  @Override
  protected void initServices() {
    super.initServices();
    this.binance =
        ExchangeRestProxyBuilder.forInterface(
            FBinance.class, getCustomizedExchangeSpecification())
            .build();

    BinanceAuthenticated binanceAccount = ExchangeRestProxyBuilder.forInterface(
        BinanceAuthenticated.class, getExchangeSpecification())
        .build();
    this.timestampFactory =
        new BinanceTimestampFactory(
            binance, getExchangeSpecification().getResilience(), getResilienceRegistries());
    this.marketDataService = new BinanceMarketDataService(this, binance, getResilienceRegistries());
    this.tradeService = new BinanceTradeService(this, binance, getResilienceRegistries());
    this.accountService = new BinanceAccountService(this, binanceAccount, getResilienceRegistries());
  }
}
