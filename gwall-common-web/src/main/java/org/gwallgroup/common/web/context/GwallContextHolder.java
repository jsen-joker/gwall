package org.gwallgroup.common.web.context;

import java.lang.reflect.Constructor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class GwallContextHolder {
  // ~ Static fields/initializers
  // =====================================================================================

  private static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
  private static final String SYSTEM_PROPERTY = "spring.security.strategy";
  private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
  private static GwallContextHolderStrategy strategy;
  private static int initializeCount = 0;

  static {
    initialize();
  }

  // ~ Methods
  // ========================================================================================================

  /** Explicitly clears the context value from the current thread. */
  public static void clearContext() {
    strategy.clearContext();
  }

  /**
   * Obtain the current <code>SecurityContext</code>.
   *
   * @return the security context (never <code>null</code>)
   */
  public static GwallContext getContext() {
    return strategy.getContext();
  }

  /**
   * Primarily for troubleshooting purposes, this method shows how many times the class has
   * re-initialized its <code>SecurityContextHolderStrategy</code>.
   *
   * @return the count (should be one unless you've called {@link #setStrategyName(String)} to
   *     switch to an alternate strategy.
   */
  public static int getInitializeCount() {
    return initializeCount;
  }

  private static void initialize() {
    if (!StringUtils.hasText(strategyName)) {
      // Set default
      strategyName = MODE_THREADLOCAL;
    }

    if (strategyName.equals(MODE_THREADLOCAL)) {
      strategy = new ThreadLocalGwallContextHolderStrategy();
    } else {
      // Try to load a custom strategy
      try {
        Class<?> clazz = Class.forName(strategyName);
        Constructor<?> customStrategy = clazz.getConstructor();
        strategy = (GwallContextHolderStrategy) customStrategy.newInstance();
      } catch (Exception ex) {
        ReflectionUtils.handleReflectionException(ex);
      }
    }

    initializeCount++;
  }

  /**
   * Associates a new <code>SecurityContext</code> with the current thread of execution.
   *
   * @param context the new <code>SecurityContext</code> (may not be <code>null</code>)
   */
  public static void setContext(GwallContext context) {
    strategy.setContext(context);
  }

  /**
   * Changes the preferred strategy. Do <em>NOT</em> call this method more than once for a given
   * JVM, as it will re-initialize the strategy and adversely affect any existing threads using the
   * old strategy.
   *
   * @param strategyName the fully qualified class name of the strategy that should be used.
   */
  public static void setStrategyName(String strategyName) {
    GwallContextHolder.strategyName = strategyName;
    initialize();
  }

  /**
   * Allows retrieval of the context strategy. See SEC-1188.
   *
   * @return the configured strategy for storing the security context.
   */
  public static GwallContextHolderStrategy getContextHolderStrategy() {
    return strategy;
  }

  /** Delegates the creation of a new, empty context to the configured strategy. */
  public static GwallContext createEmptyContext() {
    return strategy.createEmptyContext();
  }

  @Override
  public String toString() {
    return "SecurityContextHolder[strategy='"
        + strategyName
        + "'; initializeCount="
        + initializeCount
        + "]";
  }
}
