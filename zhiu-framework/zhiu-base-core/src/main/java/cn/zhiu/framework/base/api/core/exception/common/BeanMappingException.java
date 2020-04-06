package cn.zhiu.framework.base.api.core.exception.common;

import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;
import cn.zhiu.framework.base.api.core.exception.BaseApiException;

/**
 * The type Bean mapping exception.
 *
 * @author zhuzz
 * @time 2019 /04/02 15:00:58
 */
@ExceptionCode(code = "BA0007", desc = "Bean对象映射异常")
public class BeanMappingException extends BaseApiException {

    private static final long serialVersionUID = -2934359528500883752L;

    public BeanMappingException() {
        super();
    }

    public BeanMappingException(String message) {
        super(message);
    }

    public BeanMappingException(Throwable cause) {
        super(cause);
    }

}
