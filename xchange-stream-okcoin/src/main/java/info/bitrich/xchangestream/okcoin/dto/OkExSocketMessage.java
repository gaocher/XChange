package info.bitrich.xchangestream.okcoin.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Isaac Gao
 * @Date 2020/7/13
 */
public class OkExSocketMessage {
  private String op;
  private List<String> args = new ArrayList<>();

  public static ObjectMapper objectMapper = new ObjectMapper();

  public OkExSocketMessage(String op, List<String> args) {
    this.op = op;
    this.args = args;
  }

  public OkExSocketMessage(String op, String... args) {
    this.op = op;
    this.args = Arrays.asList(args);
  }

  public String getOp() {
    return op;
  }

  public List<String> getArgs() {
    return args;
  }

  @JsonIgnore
  public String getFullString() {
    try {
      return objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
