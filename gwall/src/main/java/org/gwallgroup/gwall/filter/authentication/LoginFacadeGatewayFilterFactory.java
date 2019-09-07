package org.gwallgroup.gwall.filter.authentication;

import com.alibaba.nacos.common.util.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.LoginStatusService;
import org.gwallgroup.common.web.constants.Xheader;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 表明这是个登录接口，会在登录执行完成查找注入redis session
 *
 * @author jsen
 */
@Component
@Slf4j
public class LoginFacadeGatewayFilterFactory
    extends AbstractGatewayFilterFactory<LoginFacadeGatewayFilterFactory.Config> {

  @Reference(check = false, lazy = true)
  private LoginStatusService loginStatusService;

  public LoginFacadeGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return new InnerFilter(loginStatusService);
  }

  private static class InnerFilter implements GatewayFilter, Ordered {

    LoginStatusService loginStatusService;

    InnerFilter(LoginStatusService loginStatusService) {
      this.loginStatusService = loginStatusService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
      log.info("LoginFacadeGatewayFilterFactory");
      ServerHttpRequest req = exchange.getRequest();
      String tokenKey = getAttr(Xheader.X_TK, req, Xheader.AUTHORIZATION);
      return chain
          .filter(exchange)
          .then(
              Mono.fromRunnable(
                  () -> {
                    ServerHttpResponse response = exchange.getResponse();
                    if (response.getStatusCode() == HttpStatus.OK) {
                      Route route =
                          exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                      String serviceId = "";
                      if (route != null) {
                        serviceId = route.getId();
                      }
                      String permission = getResponseAttribute(Xheader.X_P, response, "");
                      String man = getResponseAttribute(Xheader.X_MAN, response, null);
                      String token = getResponseAttribute(tokenKey, response, null);
                      String serviceType =
                          getResponseAttribute(Xheader.X_ST, response, Xheader.DEFAULT);
                      if (permission != null && man != null && token != null) {
                        if (!loginStatusService.addSession(
                            serviceId, serviceType, token, permission, man)) {
                          response.setStatusCode(HttpStatus.BAD_REQUEST);
                        }
                      }
                    }
                  }));
    }

    @Override
    public int getOrder() {
      return 2;
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

  private static String getResponseAttribute(
      String key, ServerHttpResponse response, String defaultValue) {
    if (key == null) {
      return defaultValue;
    }
    String result = response.getHeaders().getFirst(key);
    if (result != null) {
      return result;
    }
    return defaultValue;
  }
}
