package org.gwallgroup.guard.controller;

import com.alibaba.fastjson.JSONObject;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.gwallgroup.common.utils.ResponseBase;
import org.gwallgroup.common.utils.ResponseHelp;
import org.gwallgroup.common.web.constants.Xheader;
import org.gwallgroup.common.web.context.ContextUser;
import org.gwallgroup.common.web.utils.help.AttributeHelp;
import org.gwallgroup.guard.entity.dto.TokenLoginDto;
import org.gwallgroup.guard.service.GwallAuthenticationService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录控制器 通用口令登录 验证登录 gwall 默认实现登录接口
 *
 * @author jsen
 */
@RestController
@RequestMapping(
    value = "/api/1",
    produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
@Slf4j
public class GwallGuardAuthenticationController {

  @Resource private GwallAuthenticationService gwallAuthenticationService;

  @PostMapping("/pub/token")
  public ResponseBase tokenLogin(
      HttpServletRequest request, @RequestBody TokenLoginDto tokenLoginDto) {
    String loginType = AttributeHelp.getHeader(Xheader.X_LT, request, Xheader.DEFAULT);
    String version = AttributeHelp.getHeader(Xheader.X_V, request, Xheader.DEFAULT_VERSION);
    log.debug("{} {}", loginType, version);
    return gwallAuthenticationService.login(tokenLoginDto);
  }

  @GetMapping("/auth/currentUser")
  public ResponseBase currentUser(ContextUser contextUser) {
    JSONObject object =
        JSONObject.parseObject(
            "{\"name\":\"Serati Ma\",\"avatar\":\"https://gw.alipayobjects.com/zos/antfincdn/XAosXuNZyF/BiazfanxmamNRoxxVxka.png\",\"userid\":\"00000001\",\"email\":\"antdesign@alipay.com\",\"signature\":\"海纳百川，有容乃大\",\"title\":\"交互专家\",\"group\":\"蚂蚁金服－某某某事业群－某某平台部－某某技术部－UED\",\"tags\":[{\"key\":\"0\",\"label\":\"很有想法的\"},{\"key\":\"1\",\"label\":\"专注设计\"},{\"key\":\"2\",\"label\":\"辣~\"},{\"key\":\"3\",\"label\":\"大长腿\"},{\"key\":\"4\",\"label\":\"川妹子\"},{\"key\":\"5\",\"label\":\"海纳百川\"}],\"notifyCount\":12,\"unreadCount\":11,\"country\":\"China\",\"geographic\":{\"province\":{\"label\":\"浙江省\",\"key\":\"330000\"},\"city\":{\"label\":\"杭州市\",\"key\":\"330100\"}},\"address\":\"西湖区工专路 77 号\",\"phone\":\"0752-268888888\"}");
    return ResponseHelp.simpleSucceed().append(object);
  }
}
