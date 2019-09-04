package org.gwallgroup.gwall.filter.authorization;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.setResponseStatus;

import org.apache.dubbo.config.annotation.Reference;
import org.gwallgroup.common.dubbo.SignService;
import org.gwallgroup.gwall.utils.Sign;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

/**
 * 开发api，验证
 *
 * @author jsen
 */
@Component
public class SignGatewayFilterFactory
    extends AbstractGatewayFilterFactory<SignGatewayFilterFactory.Config> {

  @Reference(check = false, lazy = true)
  private SignService signService;

  public SignGatewayFilterFactory() {
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
      System.out.println("OpenApiGatewayFilterFactory");
      ServerHttpRequest req = exchange.getRequest();
      if (Sign.checkSign(req.getQueryParams(), signService)) {
        return chain.filter(exchange);
      } else {
        setResponseStatus(exchange, HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
      }
    };
  }

  public static class Config {}
}
