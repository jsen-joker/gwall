package org.gwallgroup.guard.controller;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.RouteService;
import org.gwallgroup.common.entity.GRouteDefinition;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/29 9:33 AM
 */
@RestController
@RequestMapping("/api/1/auth/gateway")
public class GatewayController {
  @Reference(check = false, lazy = true)
  private RouteService routeService;

  private static final Log log = LogFactory.getLog(GatewayController.class);

  @PostMapping("/refresh")
  public ResponseBase refresh() {
    routeService.refresh();
    return ResponseHelp.prefabSimpleSucceed();
  }

  @GetMapping("/globalfilters")
  public ResponseBase globalfilters() {
    return ResponseHelp.prefabSimpleSucceedData(routeService.globalfilters());
  }

  @GetMapping("/routefilters")
  public ResponseBase routefilers() {
    return ResponseHelp.prefabSimpleSucceedData(routeService.routefilers());
  }

  @GetMapping("/routes")
  public ResponseBase routes(
      @RequestParam(value = "queryId", required = false) String id,
      @RequestParam(value = "queryService", required = false) String service,
      @RequestParam(value = "status", required = false) Integer status) {
    Stream<GRouteDefinition> gRouteDefinitions = routeService.routes().stream();

    if (id != null) {
      gRouteDefinitions =
          gRouteDefinitions.filter(
              gRouteDefinition ->
                  gRouteDefinition.getId() != null && gRouteDefinition.getId().contains(id));
    }
    if (service != null) {
      gRouteDefinitions =
          gRouteDefinitions.filter(
              gRouteDefinition -> gRouteDefinition.getUri().toString().contains(service));
    }
    if (status != null) {
      gRouteDefinitions =
          gRouteDefinitions.filter(gRouteDefinition -> gRouteDefinition.getStatus() == status);
    }
    return ResponseHelp.prefabSimpleSucceedData(gRouteDefinitions.collect(Collectors.toList()));
  }

  @PostMapping("/routes")
  @SuppressWarnings("unchecked")
  public ResponseBase save(@RequestBody GRouteDefinition route) {
    return ResponseHelp.prefabSimpleSucceedData(routeService.save(route));
  }

  @DeleteMapping("/routes/{id}")
  public ResponseBase delete(@PathVariable String id) {
    return ResponseHelp.prefabSimpleSucceedData(routeService.delete(id));
  }

  @GetMapping("/routes/{id}")
  public ResponseBase route(@PathVariable String id) {
    return ResponseHelp.prefabSimpleSucceedData(routeService.route(id));
  }

  @GetMapping("/routes/{id}/combinedfilters")
  public ResponseBase combinedfilters(@PathVariable String id) {
    return ResponseHelp.prefabSimpleSucceedData(routeService.combinedfilters(id));
  }
}
