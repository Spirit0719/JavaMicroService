package cn.zhiu.framework.base.api.core.interceptor.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.lang.annotation.Annotation;

public abstract class BaseHandlerInterceptorAdapter extends HandlerInterceptorAdapter {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected <T extends Annotation> T getAnnotation(HandlerMethod handlerMethod, Class<T> annotationType) {
        return this.getAnnotation(handlerMethod, annotationType, false);
    }

    protected <T extends Annotation> T getAnnotation(HandlerMethod handlerMethod, Class<T> annotationType, boolean searchClassType) {
        T annotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), annotationType);
        if (annotation == null && searchClassType) {
            annotation = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), annotationType);
        }
        return annotation;
    }
}
