package org.gwallgroup.common.entity;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import org.gwallgroup.common.utils.NameUtils;
import org.gwallgroup.common.utils.serialize.PredicateSerializer;
import org.springframework.validation.annotation.Validated;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/29 7:06 PM
 */
@Validated
@JsonSerialize(using = PredicateSerializer.class)
public class GPredicateDefinition implements Serializable {
  @NotNull private String name;
  private Map<String, String> args = new LinkedHashMap<>();

  public GPredicateDefinition() {}

  public GPredicateDefinition(String text) {
    int eqIdx = text.indexOf('=');
    if (eqIdx <= 0) {
      throw new ValidationException(
          "Unable to parse PredicateDefinition text '"
              + text
              + "'"
              + ", must be of the form name=value");
    }
    setName(text.substring(0, eqIdx));

    String[] args = tokenizeToStringArray(text.substring(eqIdx + 1), ",");

    for (int i = 0; i < args.length; i++) {
      this.args.put(NameUtils.generateName(i), args[i]);
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, String> getArgs() {
    return args;
  }

  public void setArgs(Map<String, String> args) {
    this.args = args;
  }

  public void addArg(String key, String value) {
    this.args.put(key, value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GPredicateDefinition that = (GPredicateDefinition) o;
    return Objects.equals(name, that.name) && Objects.equals(args, that.args);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, args);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("PredicateDefinition{");
    sb.append("name='").append(name).append('\'');
    sb.append(", args=").append(args);
    sb.append('}');
    return sb.toString();
  }
}
