package org.gwallgroup.common.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 所有 账户注入都继承这个类
 *
 * @author jsen
 * @version 1.0
 * @date 2019/8/30 11:00 AM
 */
@Data
@Accessors(chain = true)
public class AccountContext implements Serializable {

  /** account id */
  private Long id;
}
