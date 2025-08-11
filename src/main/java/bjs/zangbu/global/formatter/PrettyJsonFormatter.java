package bjs.zangbu.global.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

// Log -> Json 형식으로 Formatting
public class PrettyJsonFormatter {

  public static final ObjectMapper JSON = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

  public static String toPrettyJson(Object o) {
    try {
      return JSON.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    } catch (Exception e) {
      return String.valueOf(o);
    }
  }


}
