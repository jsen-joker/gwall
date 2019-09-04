package org.gwallgroup.guard.service;

import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.guard.entity.dto.TokenLoginDto;

public interface GwallAuthenticationService {

  /**
   * 使用token 登录
   *
   * @param tokenLoginDto token dto
   * @return res
   */
  ResponseBase login(TokenLoginDto tokenLoginDto);

}
