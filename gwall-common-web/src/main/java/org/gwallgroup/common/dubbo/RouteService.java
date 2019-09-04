package org.gwallgroup.common.dubbo;

import org.gwallgroup.common.entity.GRouteDefinition;

import java.util.Collection;
import java.util.HashMap;

/**
 * route 操作接口 网关实现 guard 调用
 *
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 7:48 PM
 */
public interface RouteService {

  /** 更新 */
  void refresh();

  HashMap<String, Object> globalfilters();

  HashMap<String, Object> routefilers();

  Collection<GRouteDefinition> routes();

  Collection<GRouteDefinition> save(GRouteDefinition route);

  Collection<GRouteDefinition> delete(String id);

  GRouteDefinition route(String id);

  HashMap<String, Object> combinedfilters(String id);
}
