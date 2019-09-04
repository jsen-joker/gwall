package org.gwallgroup.common.web.context;

public class GwallContextImpl implements GwallContext {

  private static final long serialVersionUID = 0;

  // ~ Instance fields
  // ================================================================================================

  private ContextUser authentication;

  public GwallContextImpl() {}

  public GwallContextImpl(ContextUser authentication) {
    this.authentication = authentication;
  }

  // ~ Methods
  // ========================================================================================================

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GwallContextImpl) {
      GwallContextImpl test = (GwallContextImpl) obj;

      if ((this.getAuthentication() == null) && (test.getAuthentication() == null)) {
        return true;
      }

      if ((this.getAuthentication() != null)
          && (test.getAuthentication() != null)
          && this.getAuthentication().equals(test.getAuthentication())) {
        return true;
      }
    }

    return false;
  }

  @Override
  public ContextUser getAuthentication() {
    return authentication;
  }

  @Override
  public int hashCode() {
    if (this.authentication == null) {
      return -1;
    } else {
      return this.authentication.hashCode();
    }
  }

  @Override
  public void setAuthentication(ContextUser authentication) {
    this.authentication = authentication;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(super.toString());

    if (this.authentication == null) {
      sb.append(": Null authentication");
    } else {
      sb.append(": Authentication: ").append(this.authentication);
    }

    return sb.toString();
  }
}
