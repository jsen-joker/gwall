package org.gwallgroup.common.web.constants;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/29 10:53 AM
 */
public interface Xheader {
  String X_X = "x-x";
  String X_ST = "x-st";
  String X_LT = "x-lt";
  String X_V = "x-v";
  String X_TK = "x-tk";
  String X_P = "x-p";
  /** redis session 中保持的用户 */
  String X_MAN = "x-man";

  String AUTHORIZATION = "Authorization";
  String AC = "ac";
  String DEFAULT = "default";
  String DEFAULT_VERSION = "0";
  String DEFAULT_NULL = null;

  /** 返回给用户的 token 值 */
  String TOKEN = "token";
}
