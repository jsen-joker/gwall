package org.gwallgroup.guard.repository;

import org.gwallgroup.guard.entity.po.GwallAccount;
import org.springframework.data.repository.Repository;

/**
 * @author jsen
 * @version 1.0
 * @date 2019/8/30 11:12 AM
 */
public interface GwallAccountRepository extends Repository<GwallAccount, Long> {
  /**
   * 根据名字获取
   *
   * @param name name
   * @return result
   */
  GwallAccount findFirstByName(String name);
}
