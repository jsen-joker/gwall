package org.gwallgroup.guard.entity.dto;

import lombok.Data;

/** 用户token登录 dto */
@Data
public class TokenLoginDto {

  private String serviceType;
  private String loginType;
  private String version;

  private String principal;
  private String token;
}
