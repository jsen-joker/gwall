package org.gwallgroup.authorization.service.impl;

import org.apache.dubbo.config.annotation.Service;
import org.gwallgroup.common.dubbo.SignService;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/30 1:34 PM
 */
@Service
public class SignServiceImpl implements SignService {
  @Override
  public String secret(String appKey) {
    return "secret";
  }
}
