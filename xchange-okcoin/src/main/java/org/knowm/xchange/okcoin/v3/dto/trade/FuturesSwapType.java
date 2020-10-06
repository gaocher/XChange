package org.knowm.xchange.okcoin.v3.dto.trade;

import static org.knowm.xchange.dto.Order.PositionOrderFlags.CLOSE_POSITION;
import static org.knowm.xchange.dto.Order.PositionOrderFlags.OPEN_POSITION;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.Order.OrderType;

/** 1:open long 2:open short 3:close long 4:close short */
@AllArgsConstructor
public enum FuturesSwapType {
  open_long("1"),
  open_short("2"),
  close_long("3"),
  close_short("4");

  private final String value;

  @JsonValue
  public String getValue() {
    return value;
  }

  public boolean sell() {
    return this == open_short || this == close_long;
  }

  public static FuturesSwapType to(Order order) {
    boolean closePosition = order.hasFlag(CLOSE_POSITION);
    boolean openPosition = order.hasFlag(OPEN_POSITION);
    if (closePosition == false && openPosition == false) {
      throw new IllegalArgumentException("Not found position order flag");
    }
    if (closePosition == true && openPosition == true) {
      throw new IllegalArgumentException("position order flag impossible for all true");
    }
    if (openPosition) {
      return order.getType() == OrderType.BID ? open_long : open_short;
    } else {
      return order.getType() == OrderType.BID ? close_long : close_short;
    }
  }
}
