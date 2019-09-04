package org.gwallgroup.authorization.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.dubbo.config.annotation.Service;
import org.gwallgroup.common.dubbo.AccessService;
import org.springframework.data.redis.core.ValueOperations;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 6:38 PM
 */
@Service
public class AccessServiceImpl implements AccessService {
  private static final String AND = ((char) 0) + "";

  @Resource(name = "redisTemplate")
  private ValueOperations<String, String> valueOperations;

  private static final Map<String, List<String>> cache = Maps.newHashMap();

  @Override
  public boolean isAccess(String serviceType, String version, String key, String permissions) {
    String needPermissions = valueOperations.get(key);
    if (needPermissions == null) {
      return true;
    }
    List<String> nps = cache.get(key);
    if (nps == null) {
      nps = Lists.newArrayList(needPermissions.split(AND));
      cache.put(key, nps);
    }
    return nps.contains(permissions);
  }
}
