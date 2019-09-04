package org.gwallgroup.gwall.entity.mongo;

import com.google.common.collect.Lists;
import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.Data;
import org.gwallgroup.common.entity.GFilterDefinition;
import org.gwallgroup.common.entity.GPredicateDefinition;
import org.gwallgroup.common.entity.GRouteDefinition;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.lang.Nullable;

/** @author jsen */
@Document(collection = "route")
@Data
public class MongoRouteDefinition extends RouteDefinition {
  @Id
  @Field("_id")
  private String id = UUID.randomUUID().toString();

  private Date createdAt;

  private Date updatedAt;

  private String desc;

  private int status;

  private boolean system;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    super.setId(id);
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return super.toString();
  }

  public boolean getSystem() {
    return system;
  }

  public static GRouteDefinition toGRouteDefinition(
      @Nullable MongoRouteDefinition routeDefinition) {
    if (routeDefinition == null) {
      return null;
    }
    GRouteDefinition gRouteDefinition = new GRouteDefinition();
    gRouteDefinition.setId(routeDefinition.getId());
    gRouteDefinition.setUri(routeDefinition.getUri());
    gRouteDefinition.setOrder(routeDefinition.getOrder());
    gRouteDefinition.setCreatedAt(routeDefinition.getCreatedAt());
    gRouteDefinition.setUpdatedAt(routeDefinition.getUpdatedAt());
    gRouteDefinition.setStatus(routeDefinition.getStatus());
    gRouteDefinition.setDesc(routeDefinition.getDesc());
    gRouteDefinition.setSystem(routeDefinition.getSystem());

    if (routeDefinition.getFilters() != null) {
      List<GFilterDefinition> fds = Lists.newArrayList();
      for (FilterDefinition item : routeDefinition.getFilters()) {
        GFilterDefinition fd = new GFilterDefinition();
        fd.setName(item.getName());
        fd.setArgs(item.getArgs());
        fds.add(fd);
      }
      gRouteDefinition.setFilters(fds);
    }
    if (routeDefinition.getPredicates() != null) {
      List<GPredicateDefinition> pds = Lists.newArrayList();
      for (PredicateDefinition item : routeDefinition.getPredicates()) {
        GPredicateDefinition pd = new GPredicateDefinition();
        pd.setName(item.getName());
        pd.setArgs(item.getArgs());
        pds.add(pd);
      }
      gRouteDefinition.setPredicates(pds);
    }
    return gRouteDefinition;
  }

  public static MongoRouteDefinition fromGRouteDefinition(GRouteDefinition route) {
    MongoRouteDefinition rd = new MongoRouteDefinition();
    rd.setId(route.getId());
    rd.setOrder(route.getOrder());
    rd.setDesc(route.getDesc());
    rd.setStatus(route.getStatus());
    String url = route.getUri().toString();
    rd.setUri(URI.create(url));
    if (route.getFilters() != null) {
      List<FilterDefinition> fds = Lists.newArrayList();
      for (GFilterDefinition item : route.getFilters()) {
        FilterDefinition fd = new FilterDefinition();
        fd.setName(item.getName());
        fd.setArgs(item.getArgs());
        fds.add(fd);
      }
      rd.setFilters(fds);
    }
    if (route.getPredicates() != null) {
      List<PredicateDefinition> pds = Lists.newArrayList();
      for (GPredicateDefinition item : route.getPredicates()) {
        PredicateDefinition pd = new PredicateDefinition();
        pd.setName(item.getName());
        pd.setArgs(item.getArgs());
        pds.add(pd);
      }
      rd.setPredicates(pds);
    }
    return rd;
  }
}
