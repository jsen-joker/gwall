package org.gwallgroup.common.web.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gwallgroup.common.web.filter.GwallFilter;
import org.gwallgroup.common.web.filter.MappedFilter;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

/** @author jsen */
public class GwallContextFilter extends OncePerRequestFilter {

  private UrlPathHelper urlPathHelper = new UrlPathHelper();
  private PathMatcher pathMatcher = new AntPathMatcher();
  private final List<GwallFilter> filters = new ArrayList<>();

  public void addFilters(List<GwallFilter> filters) {
    this.filters.addAll(filters);
  }

  //  private static final String SECURITY_IGNORE_URLS_SPILT_CHAR = ",";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    //    InterceptorRegistry
    String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
    for (GwallFilter interceptor : this.filters) {
      if (interceptor instanceof MappedFilter) {
        MappedFilter mappedInterceptor = (MappedFilter) interceptor;
        if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
          mappedInterceptor.getInterceptor().matchIgnore(request, response, filterChain);
        } else {
          mappedInterceptor.getInterceptor().dismatchCheck(request, response, filterChain);
        }
      } else {
        interceptor.matchIgnore(request, response, filterChain);
      }
    }
  }
}
