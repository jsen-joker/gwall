package org.gwallgroup.guard.utils;

import java.util.UUID;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/31 4:46 PM
 */
public class SessionUtil {
  public static String getSessionId() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
