package org.gwallgroup.common.utils.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import org.gwallgroup.common.entity.GPredicateDefinition;

public class PredicateSerializer extends JsonSerializer<GPredicateDefinition> {

  @Override
  public void serialize(
      GPredicateDefinition gPredicateDefinition,
      JsonGenerator jsonGenerator,
      SerializerProvider serializerProvider)
      throws IOException {
    FilterSerializer.dump(
        jsonGenerator, gPredicateDefinition.getName(), gPredicateDefinition.getArgs());
  }
}
