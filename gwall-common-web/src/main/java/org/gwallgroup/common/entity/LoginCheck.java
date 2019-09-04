package org.gwallgroup.common.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 登录结果封装
 * @author jsen
 * @version 1.0
 * @date 2019/8/28 6:28 PM
 */
@Data
@Accessors(chain = true)
public class LoginCheck implements Serializable {
  /** http code */
  private int code;

  /** permissions, split with \0 */
  private String permissions;

  private String xMan;
}
