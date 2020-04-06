package cn.zhiu.framework.base.api.core.annotation.request;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 映射POST请求体中JSON项第一层级的键值对
 * 对POST请求体中
 *
 * @author zhuzz
 * @time 2019 /04/12 00:35:17
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestBodyParam {


    /**
     * 是否必须
     *
     * @return the boolean
     *
     * @author zhuzz
     * @time 2019 /04/12 00:42:03
     */
    boolean required() default true;

    /**
     * Value string.
     *
     * @return the string
     *
     * @author zhuzz
     * @time 2019 /04/12 00:42:03
     */
    @AliasFor("name")
    String value() default "";

    /**
     * Name string.
     *
     * @return the string
     *
     * @author zhuzz
     * @time 2019 /04/12 00:42:03
     */
    @AliasFor("value")
    String name() default "";

  
}
