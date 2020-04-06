package cn.zhiu.framework.base.api.core.exception.common;

import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;
import cn.zhiu.framework.base.api.core.exception.BaseApiException;

@ExceptionCode(code = "CT0001", desc = "客户端类型不支持", recoverDesc = true)
public class ClientTypeNotSupportException extends BaseApiException {

    private static final long serialVersionUID = -5074516108532368725L;
}
