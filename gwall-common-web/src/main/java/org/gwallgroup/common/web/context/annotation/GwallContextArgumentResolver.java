package org.gwallgroup.common.web.context.annotation;

import java.lang.annotation.Annotation;
import org.gwallgroup.common.web.context.ContextUser;
import org.gwallgroup.common.web.context.GwallContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class GwallContextArgumentResolver implements HandlerMethodArgumentResolver {

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#
   * supportsParameter (org.springframework.core.MethodParameter)
   */
  @Override
  public boolean supportsParameter(@NotNull MethodParameter parameter) {
    return findMethodAnnotation(InjectContextUser.class, parameter) != null
        || parameter.getParameterType().isAssignableFrom(ContextUser.class);
  }

  /*
   * (non-Javadoc)
   *
   * @see org.springframework.web.method.support.HandlerMethodArgumentResolver#
   * resolveArgument (org.springframework.core.MethodParameter,
   * org.springframework.web.method.support.ModelAndViewContainer,
   * org.springframework.web.context.request.NativeWebRequest,
   * org.springframework.web.bind.support.WebDataBinderFactory)
   */
  @Override
  public Object resolveArgument(
      MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory)
      throws Exception {

    return GwallContextHolder.getContext().getAuthentication();
  }

  /**
   * Obtains the specified {@link Annotation} on the specified {@link MethodParameter}.
   *
   * @param annotationClass the class of the {@link Annotation} to find on the {@link
   *     MethodParameter}
   * @param parameter the {@link MethodParameter} to search for an {@link Annotation}
   * @return the {@link Annotation} that was found or null.
   */
  private <T extends Annotation> T findMethodAnnotation(
      Class<T> annotationClass, MethodParameter parameter) {
    T annotation = parameter.getParameterAnnotation(annotationClass);
    if (annotation != null) {
      return annotation;
    }
    Annotation[] annotationsToSearch = parameter.getParameterAnnotations();
    for (Annotation toSearch : annotationsToSearch) {
      annotation = AnnotationUtils.findAnnotation(toSearch.annotationType(), annotationClass);
      if (annotation != null) {
        return annotation;
      }
    }
    return null;
  }
}
