package org.gwallgroup.common.web.context;

import org.springframework.util.Assert;

final class ThreadLocalGwallContextHolderStrategy implements GwallContextHolderStrategy {
  // ~ Static fields/initializers
  // =====================================================================================

  private static final ThreadLocal<GwallContext> contextHolder = new ThreadLocal<>();

  // ~ Methods
  // ========================================================================================================

  @Override
  public void clearContext() {
    contextHolder.remove();
  }

  @Override
  public GwallContext getContext() {
    GwallContext ctx = contextHolder.get();

    if (ctx == null) {
      ctx = createEmptyContext();
      contextHolder.set(ctx);
    }

    return ctx;
  }

  @Override
  public void setContext(GwallContext context) {
    Assert.notNull(context, "Only non-null SecurityContext instances are permitted");
    contextHolder.set(context);
  }

  @Override
  public GwallContext createEmptyContext() {
    return new GwallContextImpl();
  }
}
