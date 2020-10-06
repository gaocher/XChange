package org.knowm.xchange.okcoin;

import org.knowm.xchange.BaseExchange;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.exceptions.ExchangeException;
import org.knowm.xchange.okcoin.v3.service.OkexAccountService;
import org.knowm.xchange.okcoin.v3.service.OkexFutureTradeService;
import org.knowm.xchange.okcoin.v3.service.OkexMarketDataService;
import org.knowm.xchange.okcoin.v3.service.OkexTradeService;
import org.knowm.xchange.utils.AuthUtils;
import si.mazi.rescu.SynchronizedValueFactory;

public class OkexExchangeV3 extends BaseExchange {
  public static String USE_FUTURES_SPEC_ITEM = "Use_Futures";

  @Override
  protected void initServices() {
    Object userFutures = exchangeSpecification
        .getExchangeSpecificParametersItem(USE_FUTURES_SPEC_ITEM);
    if (userFutures != null && userFutures.equals(true)) {
      this.marketDataService = null;
      this.accountService = null;
      this.tradeService = new OkexFutureTradeService(this);
    } else {
      this.marketDataService = new OkexMarketDataService(this);
      this.accountService = new OkexAccountService(this);
      this.tradeService = new OkexTradeService(this);
    }
  }

  @Override
  public ExchangeSpecification getDefaultExchangeSpecification() {
    ExchangeSpecification spec = new ExchangeSpecification(this.getClass().getCanonicalName());
    spec.setSslUri("https://www.okex.com");
    spec.setHost("www.okex.com");
    spec.setExchangeName("OKEx");
    spec.setExchangeDescription("OKEx is a globally oriented crypto-currency trading platform.");
    AuthUtils.setApiAndSecretKey(spec, "okex");
    return spec;
  }

  @Override
  public SynchronizedValueFactory<Long> getNonceFactory() {
    throw new ExchangeException("Nonce value not supported at OKEx.");
  }
}
