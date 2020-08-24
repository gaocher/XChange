package info.bitrich.xchangestream.okcoin;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * @author Isaac Gao
 * @Date 2020/7/13
 */
@Data
public class OkExMarketDataMessage {
  private String table;
  private List<DataNode> data = new ArrayList<>();


  @Data
  public static class DataNode {
    private String instrument_id;
    private String price;
    private String side;
    private String size;
    private String timestamp;
    private String trade_id;
  }
}
