package org.gwallgroup.common.web.filter;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.StringUtils;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.gwallgroup.common.web.constants.Xheader;
import org.gwallgroup.common.web.context.ContextUser;
import org.gwallgroup.common.web.context.GwallContextHolder;
import org.gwallgroup.common.web.utils.help.AttributeHelp;

public class ContextInjectFilter implements GwallFilter {

  private static final String SERVICE_SPILT_CHAR = ",";
  private Set<String> allowServiceTypes = Sets.newHashSet();

  public ContextInjectFilter(String serviceTypes) {
    if (StringUtils.isNotBlank(serviceTypes)) {
      Set<String> st = Sets.newHashSet();
      st.addAll(Arrays.asList(serviceTypes.split(SERVICE_SPILT_CHAR)));
      allowServiceTypes = Collections.unmodifiableSet(st);
    }
    if (allowServiceTypes.isEmpty()) {
      allowServiceTypes = null;
    }
  }

  @Override
  public void matchIgnore(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    injectContext(request, response, filterChain);
  }

  @Override
  public void dismatchCheck(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (allowServiceTypes == null) {
      injectContext(request, response, filterChain);
    } else {
      String hst = AttributeHelp.getHeader(Xheader.X_ST, request, null);
      if (hst == null || !allowServiceTypes.contains(hst)) {
        response.setStatus(401);
      } else {
        injectContext(request, response, filterChain);
      }
    }
  }

  private void injectContext(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws IOException, ServletException {
    String user = AttributeHelp.getHeader(Xheader.X_MAN, request, null);
    if (user != null) {
      JSONObject cu = JSONObject.parseObject(user);
      GwallContextHolder.getContext()
          .setAuthentication(new ContextUser().setUser(cu).setId(cu.getLong("id")));
    }
    filterChain.doFilter(request, response);
  }
}
