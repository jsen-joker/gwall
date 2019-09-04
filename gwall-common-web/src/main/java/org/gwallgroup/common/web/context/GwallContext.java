package org.gwallgroup.common.web.context;

public interface GwallContext {

  /**
   * Obtains the currently authenticated principal, or an authentication request token.
   *
   * @return the <code>Authentication</code> or <code>null</code> if no authentication information
   *     is available
   */
  ContextUser getAuthentication();

  /**
   * Changes the currently authenticated principal, or removes the authentication information.
   *
   * @param authentication the new <code>Authentication</code> token, or <code>null</code> if no
   *     further authentication information should be stored
   */
  void setAuthentication(ContextUser authentication);
}
