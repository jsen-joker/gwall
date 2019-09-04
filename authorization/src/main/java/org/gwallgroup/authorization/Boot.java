package org.gwallgroup.authorization;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDubbo(scanBasePackages = "org.gwallgroup.authorization.service")
@EnableDiscoveryClient
public class Boot {

  public static void main(String[] args) {
    SpringApplication.run(Boot.class, args);
  }
}
