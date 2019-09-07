package org.gwallgroup.authentication.service.impl;

import com.alibaba.fastjson.JSONObject;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.gwallgroup.common.dubbo.LoginStatusService;
import org.gwallgroup.common.entity.LoginCheck;
import org.gwallgroup.common.web.constants.Xheader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

@Service
@Slf4j
public class LoginStatusServiceImpl implements LoginStatusService {

  @Resource
  @Qualifier("redisSessionTemplate")
  private RedisTemplate<String, JSONObject> sessionOperations;

  @Resource
  @Qualifier("redisLoginTemplate")
  private RedisTemplate<String, String> loginOperations;

  /**
   * 检查是否登入
   *
   * @param loginType 登入类型（手机登录，账号登录等）
   * @param version token 版本 （1，2，3 etc.）
   * @param tokenKey 获取token的token key
   * @param token token 的值
   * @return http code 200 succeed
   */
  @Override
  public LoginCheck isLogin(String loginType, String version, String tokenKey, String token) {
    if (token == null) {
      return new LoginCheck().setCode(401);
    }
    return check(tokenKey, token);
  }

  @Override
  public boolean addSession(
      String routeId, String serviceType, String token, String permission, String man) {
    try {
      String idKey = "id";
      JSONObject manObj = JSONObject.parseObject(man);
      if (!manObj.containsKey(idKey)) {
        log.error("header {} json not contain id", Xheader.X_MAN);
        return false;
      }
      Long id = manObj.getLong(idKey);

      String manId = id + "-" + routeId + "-" + serviceType;
      String oldSession = loginOperations.boundValueOps(manId).get();
      if (oldSession != null) {
        sessionOperations.delete(oldSession);
      }

      JSONObject domain = new JSONObject();
      domain.put(Xheader.X_P, "");
      domain.put(Xheader.X_ST, serviceType);
      domain.put(Xheader.X_MAN, manObj);
      loginOperations.boundValueOps(manId).set(token, 6, TimeUnit.HOURS);
      sessionOperations.boundValueOps(token).set(domain, 6, TimeUnit.HOURS);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private LoginCheck check(String tokenKey, String token) {
    JSONObject exist = sessionOperations.boundValueOps(token).get();
    if (exist != null) {
      return new LoginCheck()
          .setCode(200)
          .setPermissions(exist.getString(Xheader.X_P))
          .setServiceType(exist.getString(Xheader.X_ST))
          .setXMan(exist.getString(Xheader.X_MAN));
    } else {
      return new LoginCheck().setCode(401);
    }
  }
}
