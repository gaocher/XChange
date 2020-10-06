package org.knowm.xchange.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.knowm.xchange.dto.Order.IOrderFlags;

public enum TimeInForce implements IOrderFlags {
  GTC, //normal
  FOK,
  IOC,
  GTX; //只做maker

  @JsonCreator
  public static TimeInForce getTimeInForce(String s) {
    try {
      return TimeInForce.valueOf(s);
    } catch (Exception e) {
      throw new RuntimeException("Unknown ordtime in force " + s + ".");
    }
  }
}
