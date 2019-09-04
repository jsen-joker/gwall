package org.gwallgroup.common.web.context;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gwallgroup.common.web.constants.Xheader;
import org.gwallgroup.common.web.utils.help.AttributeHelp;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author jsen
 */
public class GwallContextFilter extends OncePerRequestFilter {
  private static final String SECURITY_IGNORE_URLS_SPILT_CHAR = ",";

  private Set<String> ignoreUrls = null;
  private Set<String> allowServiceTypes = null;


  public GwallContextFilter(String ignoreUrls, String serviceTypes) {
    if (!StringUtils.isEmpty(ignoreUrls)) {
      Set<String> hst = Sets.newHashSet();
      for (String ignoreUrl : ignoreUrls.trim().split(SECURITY_IGNORE_URLS_SPILT_CHAR)) {
        hst.add(ignoreUrl.trim());
      }
      this.ignoreUrls = Collections.unmodifiableSet(hst);
    }
    if (!StringUtils.isEmpty(serviceTypes)) {
      Set<String> ast = Sets.newHashSet();
      for (String as : serviceTypes.trim().split(SECURITY_IGNORE_URLS_SPILT_CHAR)) {
        ast.add(as.trim());
      }
      allowServiceTypes = Collections.unmodifiableSet(ast);
    }
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (ignoreUrls != null && allowServiceTypes != null) {
      String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
      boolean ignorePath = ignoreUrls.contains(path);
      if (!ignorePath) {
        // 检查 service type
        String hst = AttributeHelp.getHeader(Xheader.X_ST, request, null);
        if (hst == null || !allowServiceTypes.contains(hst)) {
          response.setStatus(401);
          return;
        }
      }
    }
    String user = AttributeHelp.getHeader(Xheader.X_MAN, request, null);
    if (user != null) {
      JSONObject cu = JSONObject.parseObject(user);
      GwallContextHolder.getContext()
          .setAuthentication(new ContextUser().setUser(cu).setId(cu.getLong("id")));
    }
    filterChain.doFilter(request, response);
  }
}
