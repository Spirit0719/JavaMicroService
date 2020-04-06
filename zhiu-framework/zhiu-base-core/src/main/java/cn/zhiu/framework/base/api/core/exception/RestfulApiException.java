package cn.zhiu.framework.base.api.core.exception;


import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;

@ExceptionCode(code = "RA9999", desc = "系统内部错误", recoverDesc = true)
public class RestfulApiException extends ZhiuException {

    private static final long serialVersionUID = -728310954340028359L;

    public RestfulApiException() {
        super();
    }

    public RestfulApiException(Throwable cause) {
        super(cause);
    }

    public RestfulApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public RestfulApiException(String message) {
        super(message);
    }

}
