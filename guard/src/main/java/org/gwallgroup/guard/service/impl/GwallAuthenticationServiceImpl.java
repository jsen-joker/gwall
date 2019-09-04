package org.gwallgroup.guard.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.util.Md5Utils;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.gwallgroup.common.web.constants.Xheader;
import org.gwallgroup.common.web.utils.help.AttributeHelp;
import org.gwallgroup.guard.entity.dto.TokenLoginDto;
import org.gwallgroup.guard.entity.po.GwallAccount;
import org.gwallgroup.guard.repository.GwallAccountRepository;
import org.gwallgroup.guard.service.GwallAuthenticationService;
import org.gwallgroup.guard.utils.SessionUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;

@Service
public class GwallAuthenticationServiceImpl implements GwallAuthenticationService {

  @Resource private GwallAccountRepository gwallAccountRepository;

  @Override
  public ResponseBase login(TokenLoginDto tokenLoginDto) {
    GwallAccount exist = gwallAccountRepository.findFirstByName(tokenLoginDto.getPrincipal());
    if (exist != null) {
      String token = Md5Utils.getMD5(tokenLoginDto.getToken().getBytes(Charset.defaultCharset()));
      if (token.equals(exist.getPassword())) {
        String newSession = SessionUtil.getSessionId();
//        JSONObject domain = new JSONObject();
//        domain.put(Xheader.X_P, "");
//        domain.put(Xheader.X_MAN, JSON.toJSON(exist));
//        loginOperations.boundValueOps(exist.getId()).set(newSession, 6, TimeUnit.HOURS);
//        sessionOperations.boundValueOps(newSession).set(domain, 6, TimeUnit.HOURS);
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String tokenKey = AttributeHelp.getHeader(Xheader.X_TK, request, Xheader.AUTHORIZATION);

        HttpServletResponse response = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getResponse();
        if (response != null) {
          Cookie cookie = new Cookie(tokenKey, newSession);
          cookie.setPath("/");
          response.addCookie(cookie);
          Cookie service = new Cookie(Xheader.X_ST, "gwall");
          service.setPath("/");
          response.addCookie(service);
          response.setHeader(tokenKey, newSession);
          response.setHeader(Xheader.X_ST, "gwall");
          response.setHeader(Xheader.X_P, "");
          response.setHeader(Xheader.X_MAN, JSON.toJSONString(exist));
        }
        return ResponseHelp.simpleSucceed()
            .add(tokenKey, newSession)
            .add("currentAuthority", "admin");
      } else {
        return ResponseHelp.prefabSimpleFailed("password authentication failed");
      }
    } else {
      return ResponseHelp.prefabSimpleFailed("account authentication failed");
    }
  }
}
