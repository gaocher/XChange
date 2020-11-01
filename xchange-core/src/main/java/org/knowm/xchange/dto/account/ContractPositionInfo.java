package org.knowm.xchange.dto.account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchange.currency.Currency;
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

  public Map<CurrencyPair, ContractPosition> getCrossPositionsByCurrency(Currency currency) {
    return crossPositions.entrySet().stream()
        .filter(e -> e.getKey().contains(currency))
        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
  }

  public List<ContractPosition> getAllIsolatedPositions() {
    return allIsolatedPositions;
  }
}
