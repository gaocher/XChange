package info.bitrich.xchangestream.binance;

import lombok.extern.slf4j.Slf4j;
import org.knowm.xchange.ExchangeSpecification;
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
@Slf4j
public class ContractBinanceStreamingExchange extends BinanceStreamingExchange {

  @Override
  protected String getStreamingApiBaseUri() {
    return "wss://dstream.binance.com/";
  }

  public ExchangeSpecification getCustomizedExchangeSpecification() {

    ExchangeSpecification spec = new ExchangeSpecification(this.getClass().getCanonicalName());
    spec.setSslUri("https://dapi.binance.com");
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
            CBinanceAuthenticated.class, getCustomizedExchangeSpecification())
            .build();
    this.timestampFactory =
        new BinanceTimestampFactory(
            binance, getExchangeSpecification().getResilience(), getResilienceRegistries());
    this.marketDataService = new BinanceMarketDataService(this, binance, getResilienceRegistries());
    this.tradeService = new BinanceTradeService(this, binance, getResilienceRegistries());
    this.accountService = new BinanceAccountService(this, binance, getResilienceRegistries());
  }

  @Override
  protected void initConcreteStreamService() {
    streamingMarketDataService =
        new BinanceStreamingMarketDataService(
            streamingService,
            (BinanceMarketDataService) marketDataService,
            onApiCall,
            orderBookUpdateFrequencyParameter);
    streamingAccountService = new BinanceStreamingAccountService(userDataStreamingService);
    streamingTradeService = new ContractBinanceStreamingTradeService(userDataStreamingService);
  }
}
