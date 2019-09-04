package org.gwallgroup.common.web.utils.help;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/30 10:31 AM
 */
public class AttributeHelp {

  public static String getHeader(String key, HttpServletRequest request, String defaultValue) {
    if (key == null) {
      return defaultValue;
    }
    String result = request.getHeader(key);
    if (result != null) {
      return result;
    }
    result = request.getParameter(key);
    if (result != null) {
      return result;
    }
    for (Cookie cookie : request.getCookies()) {
      if (key.equals(cookie.getName())) {
        result = cookie.getValue();
      }
    }
    if (result != null) {
      return result;
    }
    return defaultValue;
  }
}
