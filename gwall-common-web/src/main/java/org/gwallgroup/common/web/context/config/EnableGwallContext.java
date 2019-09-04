package org.gwallgroup.common.web.context.config;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 允许 gwall 注入context
 * @author jsen
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target(value = {java.lang.annotation.ElementType.TYPE})
@Documented
@Import({GwallContextImportSelector.class})
@Configuration
public @interface EnableGwallContext {}
