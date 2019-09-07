package org.gwallgroup.common.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;

/** @author jsen */
public class MappedFilter implements GwallFilter {

  @Nullable private final String[] includePatterns;
  @Nullable private final String[] excludePatterns;
  private final GwallFilter filter;
  @Nullable private PathMatcher pathMatcher;

  public MappedFilter(@Nullable String[] includePatterns, GwallFilter filter) {
    this(includePatterns, null, filter);
  }

  public MappedFilter(
      @Nullable String[] includePatterns, @Nullable String[] excludePatterns, GwallFilter filter) {
    this.includePatterns = includePatterns;
    this.excludePatterns = excludePatterns;
    this.filter = filter;
  }

  public void setPathMatcher(@Nullable PathMatcher pathMatcher) {
    this.pathMatcher = pathMatcher;
  }

  @Nullable
  public PathMatcher getPathMatcher() {
    return this.pathMatcher;
  }

  @Nullable
  public String[] getPathPatterns() {
    return this.includePatterns;
  }

  public GwallFilter getInterceptor() {
    return this.filter;
  }

  public boolean matches(String lookupPath, PathMatcher pathMatcher) {
    PathMatcher pathMatcherToUse = this.pathMatcher != null ? this.pathMatcher : pathMatcher;
    String[] var4;
    int var5;
    int var6;
    String pattern;
    if (!ObjectUtils.isEmpty(this.excludePatterns)) {
      var4 = this.excludePatterns;
      var5 = var4.length;

      for (var6 = 0; var6 < var5; ++var6) {
        pattern = var4[var6];
        if (pathMatcherToUse.match(pattern, lookupPath)) {
          return false;
        }
      }
    }

    if (ObjectUtils.isEmpty(this.includePatterns)) {
      return true;
    } else {
      var4 = this.includePatterns;
      var5 = var4.length;

      for (var6 = 0; var6 < var5; ++var6) {
        pattern = var4[var6];
        if (pathMatcherToUse.match(pattern, lookupPath)) {
          return true;
        }
      }

      return false;
    }
  }

  @Override
  public void matchIgnore(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    this.filter.matchIgnore(request, response, filterChain);
  }

  @Override
  public void dismatchCheck(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    this.filter.dismatchCheck(request, response, filterChain);
  }
}
