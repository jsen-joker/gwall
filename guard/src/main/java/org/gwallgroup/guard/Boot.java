package org.gwallgroup.guard;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.gwallgroup.common.web.context.config.EnableGwallContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDubbo
@EnableGwallContext
@EnableDiscoveryClient
public class Boot {

  public static void main(String[] args) {
    SpringApplication.run(Boot.class, args);
  }
}
