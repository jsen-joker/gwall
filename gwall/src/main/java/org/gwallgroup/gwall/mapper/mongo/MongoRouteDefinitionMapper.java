package org.gwallgroup.gwall.mapper.mongo;

import org.gwallgroup.gwall.entity.mongo.MongoRouteDefinition;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Service;

@Service
public interface MongoRouteDefinitionMapper extends MongoRepository<MongoRouteDefinition, String> {}
