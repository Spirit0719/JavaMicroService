package cn.zhiu.framework.base.api.core.annotation.request;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestApiFieldUpdatable {
}