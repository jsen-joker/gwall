package org.gwallgroup.authentication.config.redis;

import com.alibaba.fastjson.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class RedisConfig {

  @Bean
  //  @ConditionalOnMissingBean(name = "redisTemplate")
  public RedisTemplate<String, JSONObject> redisSessionTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, JSONObject> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }

  @Bean
  //  @ConditionalOnMissingBean(name = "redisTemplate")
  public RedisTemplate<String, String> redisLoginTemplate(
      RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(redisConnectionFactory);
    return redisTemplate;
  }
}
