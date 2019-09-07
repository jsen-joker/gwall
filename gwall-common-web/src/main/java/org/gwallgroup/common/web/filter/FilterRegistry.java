package org.gwallgroup.common.web.filter;

import com.google.common.collect.Lists;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.OrderComparator;

/** @author jsen */
public class FilterRegistry {

  private final List<FilterRegistration> registrations = Lists.newArrayList();
  private static final Comparator<Object> FILTER_ORDER_COMPARATOR;

  public FilterRegistry() {}

  public FilterRegistration addFilter(GwallFilter filter) {
    FilterRegistration registration = new FilterRegistration(filter);
    this.registrations.add(registration);
    return registration;
  }

  public List<GwallFilter> getFilters() {
    return this.registrations.stream()
        .sorted(FILTER_ORDER_COMPARATOR)
        .map(FilterRegistration::getInterceptor)
        .collect(Collectors.toList());
  }

  static {
    FILTER_ORDER_COMPARATOR =
        OrderComparator.INSTANCE.withSourceProvider(
            (object) -> {
              if (object instanceof FilterRegistration) {
                FilterRegistration var10000 = (FilterRegistration) object;
                return var10000.getOrder();
              } else {
                return null;
              }
            });
  }
}
