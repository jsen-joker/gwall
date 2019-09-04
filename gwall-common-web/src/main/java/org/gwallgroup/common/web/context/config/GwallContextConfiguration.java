package org.gwallgroup.common.web.context.config;

import java.util.List;
import javax.annotation.Resource;
import org.gwallgroup.common.web.context.GwallContextFilter;
import org.gwallgroup.common.web.context.annotation.GwallContextArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author jsen
 */
public class GwallContextConfiguration implements WebMvcConfigurer {

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    GwallContextArgumentResolver authenticationPrincipalResolver =
        new GwallContextArgumentResolver();
    argumentResolvers.add(authenticationPrincipalResolver);
  }

  @Resource
  private Environment env;

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean bean = new FilterRegistrationBean();
    String ignoreUrls = env.getProperty("gwall.ignore.url", "");
    String serviceTypes = env.getProperty("gwall.allow.service.type", "");
    bean.setFilter(new GwallContextFilter(ignoreUrls, serviceTypes));
    bean.addUrlPatterns("/*");
    return bean;
  }
}
