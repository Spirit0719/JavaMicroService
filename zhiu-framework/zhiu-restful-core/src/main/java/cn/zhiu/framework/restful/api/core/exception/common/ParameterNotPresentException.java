package cn.zhiu.framework.restful.api.core.exception.common;

import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;
import cn.zhiu.framework.base.api.core.exception.RestfulApiException;

@ExceptionCode(code = "RC0003", desc = "参数必填", recoverDesc = true)
public class ParameterNotPresentException extends RestfulApiException {


    private static final long serialVersionUID = 7012696077286346436L;

    public ParameterNotPresentException() {
        super();
    }

    public ParameterNotPresentException(String message) {
        super(message);
    }
}
