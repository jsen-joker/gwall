package org.gwallgroup.common.dubbo;

/**
 * 授权接口 授权模块实现 网关调用
 *
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 6:24 PM
 */
public interface AccessService {
  /**
   * 检查是否登入
   *
   * @param serviceType 服务类型（登录的服务，一般指不同的用户数据库）
   * @param version token 版本 （1，2，3 etc.）
   * @param key 获取token的token key
   * @param permissions 目前permissions只支持一个
   * @return http code 200 succeed
   */
  boolean isAccess(String serviceType, String version, String key, String permissions);
}
