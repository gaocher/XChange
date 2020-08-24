package org.knowm.xchange.currency;

import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Objects;

/**
 * @author Isaac Gao
 * @Date 2020/8/24
 */
public class ContractCurrencyPair extends CurrencyPair {
  protected String contractNo;

  public ContractCurrencyPair(Currency base, Currency counter, String contractNo) {
    super(base, counter);
    this.contractNo = contractNo;
  }

  public ContractCurrencyPair(String baseSymbol, String counterSymbol, String contractNo) {
    super(baseSymbol, counterSymbol);
    this.contractNo = contractNo;
  }

  public ContractCurrencyPair(String currencyPair, String contractNo) {
    super(currencyPair);
    this.contractNo = contractNo;
  }

  public String getContractNo() {
    return contractNo;
  }

  @JsonValue
  @Override
  public String toString() {

    return base + "/" + counter+"_"+contractNo;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    ContractCurrencyPair that = (ContractCurrencyPair) o;
    return Objects.equals(contractNo, that.contractNo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), contractNo);
  }
}
