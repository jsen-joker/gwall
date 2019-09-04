package org.gwallgroup.common.utils;

/** @author Spencer Gibb */
public class NameUtils {
  private static final String GENERATED_NAME_PREFIX = "_genkey_";

  public static String generateName(int i) {
    return GENERATED_NAME_PREFIX + i;
  }
}
