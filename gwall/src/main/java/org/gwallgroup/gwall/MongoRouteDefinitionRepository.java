package org.gwallgroup.gwall;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.gwallgroup.gwall.entity.mongo.MongoRouteDefinition;
import org.gwallgroup.gwall.service.MongoRouteDefinitionService;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class MongoRouteDefinitionRepository implements RouteDefinitionRepository {

  @Resource private MongoRouteDefinitionService mongoRouteDefinitionService;
  private final Map<String, MongoRouteDefinition> routes = new ConcurrentSkipListMap<>();

  @PostConstruct
  public void init() {
    refresh();
    log.info("loaded route from mongodb");
  }

  public void refresh() {
    routes.putAll(
        mongoRouteDefinitionService.queryAll(1).stream()
            .collect(Collectors.toMap(MongoRouteDefinition::getId, e -> e)));
  }

  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    return Flux.fromIterable(this.routes.values());
  }

  public Collection<MongoRouteDefinition> getMongoRouteDefinitions() {
    return this.routes.values();
  }

  @Override
  public Mono<Void> save(Mono<RouteDefinition> route) {
    return route.flatMap(
        (r) -> {
          MongoRouteDefinition mongoRouteDefinition = new MongoRouteDefinition();
          mongoRouteDefinition.setId(r.getId());
          mongoRouteDefinition.setPredicates(r.getPredicates());
          mongoRouteDefinition.setFilters(r.getFilters());
          mongoRouteDefinition.setUri(r.getUri());
          mongoRouteDefinition.setOrder(r.getOrder());
          this.routes.put(
              r.getId(), mongoRouteDefinitionService.createOrUpdate(mongoRouteDefinition));
          return Mono.empty();
        });
  }

  public void save(MongoRouteDefinition route) {
    MongoRouteDefinition result = mongoRouteDefinitionService.createOrUpdate(route);
    if (result.getStatus() == 1) {
      this.routes.put(result.getId(), result);
    } else {
      this.routes.remove(result.getId());
    }
  }

  @Override
  public Mono<Void> delete(Mono<String> routeId) {
    return routeId.flatMap(
        id -> {
          if (this.routes.containsKey(id)) {
            mongoRouteDefinitionService.deleteById(id);
            this.routes.remove(id);
            return Mono.empty();
          } else {
            return Mono.defer(
                () -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId)));
          }
        });
  }

  public void delete(String routeId) {
    mongoRouteDefinitionService.deleteById(routeId);
    this.routes.remove(routeId);
  }
}
