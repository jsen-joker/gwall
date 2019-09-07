package org.gwallgroup.common.web.context.config;

import com.alibaba.nacos.client.utils.StringUtils;
import java.util.List;
import javax.annotation.Resource;
import org.gwallgroup.common.web.context.GwallContextFilter;
import org.gwallgroup.common.web.context.annotation.GwallContextArgumentResolver;
import org.gwallgroup.common.web.filter.ContextInjectFilter;
import org.gwallgroup.common.web.filter.FilterRegistration;
import org.gwallgroup.common.web.filter.FilterRegistry;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** @author jsen */
public class GwallContextConfiguration implements WebMvcConfigurer {

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    GwallContextArgumentResolver authenticationPrincipalResolver =
        new GwallContextArgumentResolver();
    argumentResolvers.add(authenticationPrincipalResolver);
  }

  @Resource private Environment env;

  private static final String SERVICE_CHECK_IGNORE_SPILT_CHAR = ",";

  @Bean
  public FilterRegistrationBean filterRegistrationBean() {
    FilterRegistrationBean<GwallContextFilter> bean = new FilterRegistrationBean<>();
    String ignoreUrls = env.getProperty("gwall.ignore.service.check.url", "");
    String serviceTypes = env.getProperty("gwall.allow.service.type", "");
    GwallContextFilter gwallContextFilter = new GwallContextFilter();

    FilterRegistry filterRegistry = new FilterRegistry();
    ContextInjectFilter contextInjectFilter = new ContextInjectFilter(serviceTypes);
    FilterRegistration filterRegistration =
        filterRegistry.addFilter(contextInjectFilter).addPathPatterns("/**");
    if (StringUtils.isNotBlank(ignoreUrls) && StringUtils.isNotBlank(serviceTypes)) {
      filterRegistration.excludePathPatterns(ignoreUrls.split(SERVICE_CHECK_IGNORE_SPILT_CHAR));
    }
    gwallContextFilter.addFilters(filterRegistry.getFilters());

    bean.setFilter(gwallContextFilter);
    bean.addUrlPatterns("/*");
    return bean;
  }
}
