package org.knowm.xchange.utils;

import java.math.BigDecimal;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.utils.PositionCalcUtils.PositionCalc.PositionCalcResult;

/**
 * @author Isaac Gao
 * @Date 2020/10/30
 */
public class PositionCalcUtils {
  public static BigDecimal HUNDRED = BigDecimal.valueOf(100);

  public static PositionCalcResult calc(OrderBook bid, OrderBook ask) {
    BigDecimal askOnePrice = bid.getAskOnePrice();
    BigDecimal bidOnePrice = ask.getBidOnePrice();
    return calc(askOnePrice, bidOnePrice);
  }

  public static PositionCalcResult settlementCalc(BigDecimal openBidPrice, BigDecimal openAskPrice, OrderBook closeBid, OrderBook closeAsk) {
    BigDecimal askOnePrice = closeBid.getAskOnePrice();
    BigDecimal bidOnePrice = closeAsk.getBidOnePrice();
    return settlementCalc(openBidPrice, openAskPrice, askOnePrice, bidOnePrice);
  }


  public static PositionCalcResult calc(BigDecimal bidPrice, BigDecimal askPrice) {
    return PositionCalc.calc(bidPrice, askPrice);
  }

  public static PositionCalcResult settlementCalc(BigDecimal openBidPrice, BigDecimal openAskPrice, BigDecimal closeBidPrice, BigDecimal closeAskPrice) {
    PositionCalcResult open = PositionCalc.calc(openBidPrice, openAskPrice);
    PositionCalcResult close = PositionCalc.calc(closeBidPrice, closeAskPrice);
    BigDecimal openCost = open.getCost();
    BigDecimal fee = open.getFee().add(close.getFee());
    BigDecimal pnl = open.getPnl().add(close.getPnl());
    BigDecimal pnlRate = pnl.divide(openCost, 8, BigDecimal.ROUND_HALF_EVEN);
    return new PositionCalcResult(openCost, pnl, fee, pnlRate);
  }

  public static class PositionCalc {
    private static BigDecimal FEE_RATE = new BigDecimal("0.0004");

    public static PositionCalcResult calc(BigDecimal bidPrice, BigDecimal askPrice) {
      BigDecimal cost = getNamedValue(bidPrice).add(getNamedValue(askPrice));
      BigDecimal fee = cost.multiply(FEE_RATE);
      BigDecimal pnl = getDiffPnl(bidPrice, askPrice).subtract(fee);
      BigDecimal pnlRate = pnl.divide(cost, 8, BigDecimal.ROUND_HALF_EVEN);
      return new PositionCalcResult(cost, pnl, fee, pnlRate);
    }

    public static BigDecimal getNamedValue(BigDecimal price) {
      return HUNDRED.divide(price, 8, BigDecimal.ROUND_HALF_EVEN);
    }

    public static BigDecimal getDiffPnl(BigDecimal bidPrice, BigDecimal askPrice) {
      return getNamedValue(bidPrice).subtract(getNamedValue(askPrice));
    }


    public static class PositionCalcResult {
      private BigDecimal cost;
      private BigDecimal pnl;
      private BigDecimal fee;
      private BigDecimal pnlRate;

      public PositionCalcResult(BigDecimal cost, BigDecimal pnl, BigDecimal fee,
          BigDecimal pnlRate) {
        this.cost = cost;
        this.pnl = pnl;
        this.fee = fee;
        this.pnlRate = pnlRate;
      }

      public BigDecimal getCost() {
        return cost;
      }

      public BigDecimal getPnl() {
        return pnl;
      }

      public BigDecimal getFee() {
        return fee;
      }

      public BigDecimal getPnlRate() {
        return pnlRate;
      }
    }


  }
}
