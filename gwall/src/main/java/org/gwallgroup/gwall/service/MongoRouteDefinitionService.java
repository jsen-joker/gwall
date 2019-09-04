package org.gwallgroup.gwall.service;

import java.util.List;
import org.gwallgroup.gwall.entity.mongo.MongoRouteDefinition;
import org.springframework.lang.Nullable;

public interface MongoRouteDefinitionService {

  /**
   * 删除
   *
   * @param id
   */
  void deleteById(String id);

  /**
   * 更新或者创建
   *
   * @param mongoRouteDefinition r
   * @return r
   */
  MongoRouteDefinition createOrUpdate(MongoRouteDefinition mongoRouteDefinition);

  /**
   * 根据id获取
   *
   * @param id
   * @return
   */
  MongoRouteDefinition findOne(String id);

  /**
   * 查出指定状态的 rd
   *
   * @return rds
   */
  List<MongoRouteDefinition> queryAll(@Nullable Integer status);
}
