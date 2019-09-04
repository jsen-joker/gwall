package org.gwallgroup.guard.controller;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.ListView;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 5:01 PM
 */
@RestController
@RequestMapping("/api/gateway/guard/100/gwall-naming")
public class NamingController {

  private NamingService namingService;
  @Resource private NacosDiscoveryProperties nacosDiscoveryProperties;

  @PostConstruct
  public void init() {
    this.namingService = nacosDiscoveryProperties.namingServiceInstance();
  }

  @GetMapping("instances")
  public ResponseBase listServices(
      @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
      @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize) {
    try {
      ListView<String> result = namingService.getServicesOfServer(pageNo, pageSize);
      return ResponseHelp.simpleSucceed().data(result.getData());
    } catch (NacosException e) {
      return ResponseHelp.simpleFailed(e.getErrMsg());
    }
  }
}
