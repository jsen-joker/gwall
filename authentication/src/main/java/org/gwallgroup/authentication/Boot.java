package org.gwallgroup.authentication;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.gwallgroup.common.web.context.config.EnableGwallContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/** @author jsen */
@SpringBootApplication
@EnableDubbo(scanBasePackages = "org.gwallgroup.authentication.service")
@EnableDiscoveryClient
public class Boot {

  public static void main(String[] args) {
    SpringApplication.run(Boot.class, args);
  }
}
