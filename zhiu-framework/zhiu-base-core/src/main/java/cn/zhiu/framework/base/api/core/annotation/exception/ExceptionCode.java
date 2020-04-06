package cn.zhiu.framework.base.api.core.annotation.exception;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionCode {
    String code();

    String desc();

    boolean recoverDesc() default false;
}
