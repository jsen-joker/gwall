package org.gwallgroup.common.web.context;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ContextUser {

  private JSONObject user;

  private Long id;
}
