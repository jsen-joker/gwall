package org.gwallgroup.common.web.filter;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;

public class FilterRegistration {

  private final GwallFilter filter;
  private final List<String> includePatterns = Lists.newArrayList();
  private final List<String> excludePatterns = Lists.newArrayList();
  @Nullable private PathMatcher pathMatcher;
  private int order = 0;

  public FilterRegistration(GwallFilter filter) {
    Assert.notNull(filter, "Filter is required");
    this.filter = filter;
  }

  public FilterRegistration addPathPatterns(String... patterns) {
    return this.addPathPatterns(Arrays.asList(patterns));
  }

  public FilterRegistration addPathPatterns(List<String> patterns) {
    this.includePatterns.addAll(patterns);
    return this;
  }

  public FilterRegistration excludePathPatterns(String... patterns) {
    return this.excludePathPatterns(Arrays.asList(patterns));
  }

  public FilterRegistration excludePathPatterns(List<String> patterns) {
    this.excludePatterns.addAll(patterns);
    return this;
  }

  public FilterRegistration pathMatcher(PathMatcher pathMatcher) {
    this.pathMatcher = pathMatcher;
    return this;
  }

  public FilterRegistration order(int order) {
    this.order = order;
    return this;
  }

  protected int getOrder() {
    return this.order;
  }

  protected GwallFilter getInterceptor() {
    if (this.includePatterns.isEmpty() && this.excludePatterns.isEmpty()) {
      return this.filter;
    } else {
      String[] include = StringUtils.toStringArray(this.includePatterns);
      String[] exclude = StringUtils.toStringArray(this.excludePatterns);
      MappedFilter mappedInterceptor = new MappedFilter(include, exclude, this.filter);
      if (this.pathMatcher != null) {
        mappedInterceptor.setPathMatcher(this.pathMatcher);
      }

      return mappedInterceptor;
    }
  }
}
