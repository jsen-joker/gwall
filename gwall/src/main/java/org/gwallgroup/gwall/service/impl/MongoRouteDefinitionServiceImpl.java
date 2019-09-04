package org.gwallgroup.gwall.service.impl;

import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.gwallgroup.gwall.entity.mongo.MongoRouteDefinition;
import org.gwallgroup.gwall.mapper.mongo.MongoRouteDefinitionMapper;
import org.gwallgroup.gwall.service.MongoRouteDefinitionService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

@Service
public class MongoRouteDefinitionServiceImpl implements MongoRouteDefinitionService {
  @Resource private MongoRouteDefinitionMapper mongoRouteDefinitionMapper;
  @Resource private MongoTemplate mongoTemplate;

  @Override
  public void deleteById(String id) {
    mongoRouteDefinitionMapper.deleteById(id);
  }

  @Override
  public MongoRouteDefinition createOrUpdate(MongoRouteDefinition mongoRouteDefinition) {
    if (mongoRouteDefinitionMapper.existsById(mongoRouteDefinition.getId())) {
      // do update
      Query query = new Query(Criteria.where("id").is(mongoRouteDefinition.getId()));
      Update update = new Update();
      update.set("_id", mongoRouteDefinition.getId());
      update.set("predicates", mongoRouteDefinition.getPredicates());
      update.set("filters", mongoRouteDefinition.getFilters());
      update.set("uri", mongoRouteDefinition.getUri());
      update.set("order", mongoRouteDefinition.getOrder());
      update.set("updatedAt", new Date());
      update.set("desc", mongoRouteDefinition.getDesc());
      update.set("status", mongoRouteDefinition.getStatus());
      mongoTemplate.updateFirst(query, update, MongoRouteDefinition.class);
      return mongoRouteDefinitionMapper
          .findById(mongoRouteDefinition.getId())
          .orElse(mongoRouteDefinition);
    } else {
      // do create
      mongoRouteDefinition.setCreatedAt(new Date());
      mongoRouteDefinition.setUpdatedAt(mongoRouteDefinition.getCreatedAt());
      return mongoRouteDefinitionMapper.save(mongoRouteDefinition);
    }
  }

  @Override
  public MongoRouteDefinition findOne(String id) {
    return mongoRouteDefinitionMapper.findById(id).orElse(null);
  }

  @Override
  public List<MongoRouteDefinition> queryAll(@Nullable Integer status) {
    if (status != null) {
      return mongoTemplate.find(
          new Query(Criteria.where("status").is(status)), MongoRouteDefinition.class);
    }
    return mongoRouteDefinitionMapper.findAll();
  }
}
