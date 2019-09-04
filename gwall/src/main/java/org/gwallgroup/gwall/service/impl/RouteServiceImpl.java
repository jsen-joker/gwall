package org.gwallgroup.gwall.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.Service;
import org.gwallgroup.common.dubbo.RouteService;
import org.gwallgroup.common.entity.GRouteDefinition;
import org.gwallgroup.gwall.MongoRouteDefinitionRepository;
import org.gwallgroup.gwall.entity.mongo.MongoRouteDefinition;
import org.gwallgroup.gwall.service.MongoRouteDefinitionService;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import reactor.core.publisher.Flux;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 7:50 PM
 */
@Service
public class RouteServiceImpl implements RouteService, ApplicationEventPublisherAware {

  private static final Log log = LogFactory.getLog(RouteServiceImpl.class);
  @Resource private List<GlobalFilter> globalFilters;
  @Resource private List<GatewayFilterFactory> gatewayFilters;
  @Resource private MongoRouteDefinitionRepository routeDefinitionWriter;
  @Resource private MongoRouteDefinitionService mongoRouteDefinitionService;
  @Resource private RouteLocator routeLocator;
  private ApplicationEventPublisher publisher;

  @Override
  public void refresh() {
    routeDefinitionWriter.refresh();
    this.publisher.publishEvent(new RefreshRoutesEvent(this));
  }

  @Override
  public HashMap<String, Object> globalfilters() {
    return this.getNamesToOrders(this.globalFilters);
  }

  @Override
  public HashMap<String, Object> routefilers() {
    return this.getNamesToOrders(this.gatewayFilters);
  }

  @Override
  public Collection<GRouteDefinition> routes() {
    return mongoRouteDefinitionService.queryAll(null).stream()
        .map(MongoRouteDefinition::toGRouteDefinition)
        .collect(Collectors.toList());
  }

  @Override
  public Collection<GRouteDefinition> save(GRouteDefinition route) {
    MongoRouteDefinition rd = MongoRouteDefinition.fromGRouteDefinition(route);
    log.debug("Saving route: " + rd);
    this.routeDefinitionWriter.save(rd);
    return routes();
  }

  @Override
  public Collection<GRouteDefinition> delete(String id) {
    this.routeDefinitionWriter.delete(id);
    return routes();
  }

  @Override
  public GRouteDefinition route(String id) {
    return MongoRouteDefinition.toGRouteDefinition(mongoRouteDefinitionService.findOne(id));
  }

  @Override
  public HashMap<String, Object> combinedfilters(String id) {
    return this.routeLocator
        .getRoutes()
        .filter((route) -> route.getId().equals(id))
        .reduce(new HashMap<String, Object>(), this::putItem)
        .block();
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
    this.publisher = applicationEventPublisher;
  }

  private <T> HashMap<String, Object> getNamesToOrders(List<T> list) {
    return Flux.fromIterable(list).reduce(new HashMap<String, Object>(), this::putItem).block();
  }

  private HashMap<String, Object> putItem(HashMap<String, Object> map, Object o) {
    Integer order = null;
    if (o instanceof Ordered) {
      order = ((Ordered) o).getOrder();
    }

    map.put(o.toString(), order);
    return map;
  }
}
