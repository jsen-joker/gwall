package org.gwallgroup.gwall.utils;

import com.alibaba.nacos.client.utils.StringUtils;
import com.alibaba.nacos.common.util.Md5Utils;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.gwallgroup.common.dubbo.SignService;
import org.springframework.util.MultiValueMap;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/30 1:12 PM
 */
public class Sign {

  public static boolean checkSign(MultiValueMap<String, String> paraMap, SignService signService) {
    Map<String, String> pm = Maps.newHashMap();
    for (Map.Entry<String, List<String>> entry : paraMap.entrySet()) {
      List<String> v = entry.getValue();
      if (!v.isEmpty()) {
        pm.put(entry.getKey(), v.get(0));
      }
    }
    if (!pm.containsKey("sign")) {
      return false;
    }
    if (!pm.containsKey("app_key")) {
      return false;
    }
    String sign = pm.remove("sign");
    String appKey = pm.remove("app_key");
    String secret = signService.secret(appKey);
    if (secret == null) {
      return true;
    }
    String ps = getParameters(pm);
    if (ps == null) {
      return false;
    }
    return Md5Utils.getMD5((secret + ps + secret).getBytes()).equals(sign);
  }

  private static String getParameters(Map<String, String> paraMap) {
    String buff;
    try {
      List<Map.Entry<String, String>> infoIds = new ArrayList<>(paraMap.entrySet());
      // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
      infoIds.sort(Comparator.comparing(Map.Entry::getKey));
      // 构造URL 键值对的格式
      StringBuilder buf = new StringBuilder();
      for (Map.Entry<String, String> item : infoIds) {
        if (StringUtils.isNotBlank(item.getKey())) {
          String key = item.getKey();
          String val = item.getValue();
          buf.append(key).append(val);
          buf.append("&");
        }
      }
      buff = buf.toString();
      if (!buff.isEmpty()) {
        buff = buff.substring(0, buff.length() - 1);
      }
    } catch (Exception e) {
      return null;
    }
    return buff;
  }
}
