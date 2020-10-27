package org.knowm.xchange.dto.account;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchange.currency.Currency;

/**
 * @author Isaac Gao
 * @Date 2020/10/26
 */
public class ContractBalanceInfo {
  Map<Currency, ContractBalance> balances = new HashMap<>();

  public ContractBalanceInfo(List<ContractBalance> balanceList) {
    this.balances = balanceList.stream().collect(Collectors.toMap(t -> t.getCurrency(), t -> t));
  }

  public ContractBalanceInfo(Map<Currency, ContractBalance> balanceMap) {
    this.balances = balanceMap;
  }

  public ContractBalance getBalance(Currency currency) {
    return balances.get(currency);
  }
}
