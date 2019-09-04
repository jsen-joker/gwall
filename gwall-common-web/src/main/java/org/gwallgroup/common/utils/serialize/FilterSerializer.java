package org.gwallgroup.common.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.gwallgroup.common.entity.GFilterDefinition;

public class FilterSerializer extends JsonSerializer<GFilterDefinition> {

  @Override
  public void serialize(
      GFilterDefinition gFilterDefinition,
      JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider)
      throws IOException {
    dump(jsonGenerator, gFilterDefinition.getName(), gFilterDefinition.getArgs());
  }

  static void dump(JsonGenerator jsonGenerator, String name, Map<String, String> args)
      throws IOException {
    StringBuilder result = new StringBuilder(name);
    if (!args.isEmpty()) {
      result.append("=");
      for (Entry<String, String> entry : args.entrySet()) {
        result.append(entry.getValue()).append(",");
      }
      jsonGenerator.writeString(result.substring(0, result.length() - 1));
    } else {
      jsonGenerator.writeString(result.toString());
    }
  }
}
