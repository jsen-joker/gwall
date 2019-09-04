package org.gwallgroup.gwall.filter.authorization;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;

import com.alibaba.nacos.common.util.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.AccessService;
import org.gwallgroup.common.web.constants.Xheader;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 验证用户Api权限，调用授权模块
 *
 * @author jsen
 */
@Component
@Slf4j
public class AuthorizationGatewayFilterFactory
    extends AbstractGatewayFilterFactory<AuthorizationGatewayFilterFactory.Config> {

  @Reference(check = false, lazy = true)
  private AccessService accessService;

  public AuthorizationGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return new InnerFilter(accessService);
  }

  private static class InnerFilter implements GatewayFilter, Ordered {

    private AccessService accessService;

    InnerFilter(AccessService accessService) {
      this.accessService = accessService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
      log.info("AuthorizationGatewayFilterFactory");
      ServerHttpRequest req = exchange.getRequest();
      // 简单检查是否已经完成 Authentication
      String ac = req.getHeaders().getFirst(Xheader.X_X);
      if (!Xheader.AC.equals(ac)) {
        setResponseStatus(exchange, HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
      }
      String serviceType = getAttr(Xheader.X_ST, req, Xheader.DEFAULT);
      String version = getAttr(Xheader.X_V, req, Xheader.DEFAULT_VERSION);
      String permissions = getAttr(Xheader.X_P, req, Xheader.DEFAULT_NULL);
      if (accessService.isAccess(serviceType, version, req.getPath().value(), permissions)) {
        setResponseStatus(exchange, HttpStatus.OK);
        return chain.filter(exchange);
      } else {
        setResponseStatus(exchange, HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
      }
    }

    @Override
    public int getOrder() {
      return 0;
    }
  }

  public static class Config {}

  private static String getAttr(String key, ServerHttpRequest request, String defaultValue) {
    if (key == null) {
      return defaultValue;
    }
    String result = request.getHeaders().getFirst(key);
    if (result != null) {
      return result;
    }
    result = request.getQueryParams().getFirst(Md5Utils.getMD5(key.getBytes()));
    if (result != null) {
      return result;
    }
    HttpCookie cookie = request.getCookies().getFirst(key);
    if (cookie != null) {
      result = cookie.getValue();
    }
    if (result != null) {
      return result;
    }
    return defaultValue;
  }
}
