package org.gwallgroup.common.dubbo;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/30 1:29 PM
 */
public interface SignService {

  /**
   * 获取 secret
   *
   * @param appKey app key
   * @return secret
   */
  String secret(String appKey);
}
