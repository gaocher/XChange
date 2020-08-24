package info.bitrich.xchangestream.binance;

import java.io.IOException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.knowm.xchange.binance.BinanceAuthenticated;
import org.knowm.xchange.binance.dto.BinanceException;
import org.knowm.xchange.binance.dto.account.AssetDetailResponse;
import org.knowm.xchange.binance.dto.marketdata.BinanceOrderbook;
import org.knowm.xchange.binance.dto.meta.exchangeinfo.BinanceExchangeInfo;
import si.mazi.rescu.ParamsDigest;
import si.mazi.rescu.SynchronizedValueFactory;

/**
 * @author Isaac Gao
 * @Date 2020/8/23
 */

@Path("")
@Produces(MediaType.APPLICATION_JSON)
public interface FBinance extends BinanceAuthenticated {
  @Override
  @GET
  @Path("fapi/v1/depth")
  /**
   * @param symbol
   * @param limit optional, default 100; valid limits: 5, 10, 20, 50, 100, 500, 1000, 5000
   * @return
   * @throws IOException
   * @throws BinanceException
   */
  BinanceOrderbook depth(@QueryParam("symbol") String symbol, @QueryParam("limit") Integer limit)
      throws IOException, BinanceException;


  @GET
  @Path("fapi/v1/exchangeInfo")
  @Override
  BinanceExchangeInfo exchangeInfo() throws IOException;


}
