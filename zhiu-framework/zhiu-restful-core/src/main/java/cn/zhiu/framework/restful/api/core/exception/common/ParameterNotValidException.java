package cn.zhiu.framework.restful.api.core.exception.common;

import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;
import cn.zhiu.framework.base.api.core.exception.RestfulApiException;

import java.util.Objects;

@ExceptionCode(code = "RC0001", desc = "参数错误", recoverDesc = true)
public class ParameterNotValidException extends RestfulApiException {

    private static final long serialVersionUID = -6248382333890761765L;

    public ParameterNotValidException() {
        super();
    }

    public ParameterNotValidException(String message) {
        super(message);
    }


    public static void requireNonNull(Object... objs) {
        for (Object obj : objs) {
            if (Objects.isNull(obj)) {
                throw new ParameterNotValidException();
            }
        }
    }

    public static void requireNonNull(Object obj, String msg) {
        if (Objects.isNull(obj)) {
            throw new ParameterNotValidException(msg);
        }
    }
}
