package org.knowm.xchange.dto.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.knowm.xchange.currency.CurrencyPair;

/**
 * @author Isaac Gao
 * @Date 2020/10/29
 */
public class ContractPositionInfo {
  Map<CurrencyPair, ContractPosition> crossPositions = new HashMap<>();
  List<ContractPosition> allIsolatedPositions = new ArrayList<>();

  public ContractPositionInfo(
      List<ContractPosition> allPositions) {
    for (ContractPosition position : allPositions) {
      if (position.getMarginType() == MarginType.CROSSED) {
        crossPositions.put(position.getCurrencyPair(), position);
      } else {
        allIsolatedPositions.add(position);
      }
    }
  }

  public ContractPosition getCrossPosition(CurrencyPair currencyPair) {
    return crossPositions.get(currencyPair);
  }

  public List<ContractPosition> getAllIsolatedPositions() {
    return allIsolatedPositions;
  }
}
