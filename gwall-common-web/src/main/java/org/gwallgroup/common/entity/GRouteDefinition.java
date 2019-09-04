package org.gwallgroup.common.entity;

import static org.springframework.util.StringUtils.tokenizeToStringArray;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/29 7:05 PM
 */
@Validated
@Data
public class GRouteDefinition implements Serializable {
  @NotEmpty private String id = UUID.randomUUID().toString();

  @NotEmpty @Valid private List<GPredicateDefinition> predicates = new ArrayList<>();

  @Valid private List<GFilterDefinition> filters = new ArrayList<>();

  @NotNull private URI uri;

  private int order = 0;

  private Date createdAt;

  private Date updatedAt;

  private String desc;

  private int status;

  private boolean system;

  public GRouteDefinition() {}

  public GRouteDefinition(String text) {
    int eqIdx = text.indexOf('=');
    if (eqIdx <= 0) {
      throw new ValidationException(
          "Unable to parse RouteDefinition text '"
              + text
              + "'"
              + ", must be of the form name=value");
    }

    setId(text.substring(0, eqIdx));

    String[] args = tokenizeToStringArray(text.substring(eqIdx + 1), ",");

    setUri(URI.create(args[0]));

    for (int i = 1; i < args.length; i++) {
      this.predicates.add(new GPredicateDefinition(args[i]));
    }
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<GPredicateDefinition> getPredicates() {
    return predicates;
  }

  public void setPredicates(List<GPredicateDefinition> predicates) {
    this.predicates = predicates;
  }

  public List<GFilterDefinition> getFilters() {
    return filters;
  }

  public void setFilters(List<GFilterDefinition> filters) {
    this.filters = filters;
  }

  public URI getUri() {
    return uri;
  }

  public void setUri(URI uri) {
    this.uri = uri;
  }

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    GRouteDefinition GRouteDefinition = (GRouteDefinition) o;
    return Objects.equals(id, GRouteDefinition.id)
        && Objects.equals(predicates, GRouteDefinition.predicates)
        && Objects.equals(order, GRouteDefinition.order)
        && Objects.equals(uri, GRouteDefinition.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, predicates, uri);
  }

  @Override
  public String toString() {
    return "RouteDefinition{"
        + "id='"
        + id
        + '\''
        + ", predicates="
        + predicates
        + ", filters="
        + filters
        + ", uri="
        + uri
        + ", order="
        + order
        + '}';
  }

  public boolean getSystem() {
    return system;
  }
}
