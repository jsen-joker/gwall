package org.gwallgroup.gwall.filter.authentication;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;

import com.alibaba.nacos.common.util.Md5Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.LoginStatusService;
import org.gwallgroup.common.entity.LoginCheck;
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
 * 验证用户登入，调用认证模块
 *
 * @author jsen
 */
@Component
@Slf4j
public class AuthenticationGatewayFilterFactory
    extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

  @Reference(check = false, lazy = true)
  private LoginStatusService loginStatusService;

  public AuthenticationGatewayFilterFactory() {
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
      log.info("AuthenticationGatewayFilterFactory");
      ServerHttpRequest req = exchange.getRequest();
      String serviceType = getAttr(Xheader.X_ST, req, Xheader.DEFAULT);
      String loginType = getAttr(Xheader.X_LT, req, Xheader.DEFAULT);
      String version = getAttr(Xheader.X_V, req, Xheader.DEFAULT_VERSION);
      String tokenKey = getAttr(Xheader.X_TK, req, Xheader.AUTHORIZATION);
      String token = getAttr(tokenKey, req, null);
      LoginCheck loginCheck =
          loginStatusService.isLogin(serviceType, loginType, version, tokenKey, token);
      setResponseStatus(exchange, HttpStatus.resolve(loginCheck.getCode()));
      if (loginCheck.getCode() == HttpStatus.OK.value()) {
        ServerHttpRequest request =
            exchange
                .getRequest()
                .mutate()
                .header(Xheader.X_P, loginCheck.getPermissions())
                .header(Xheader.X_X, Xheader.AC)
                .header(Xheader.X_MAN, loginCheck.getXMan())
                .build();
        return chain.filter(exchange.mutate().request(request).build());
      } else {
        // 401 or else complete
        return exchange.getResponse().setComplete();
      }
    }

    @Override
    public int getOrder() {
      // 优先于LoginFacade执行
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
