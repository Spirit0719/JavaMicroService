package cn.zhiu.framework.base.api.core.exception;


import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;

@ExceptionCode(code = "BA9999", desc = "系统内部错误")
public class BaseApiException extends ZhiuException {


    private static final long serialVersionUID = -8234985561203355662L;
    private String remote;

    private String interfaceName;

    private String methodName;

    private String params;

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public BaseApiException() {
        super();
    }

    public BaseApiException(Throwable cause) {
        super(cause);
    }

    public BaseApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseApiException(String message) {
        super(message);
    }


    @Override
    public String toString() {
        return "{" +
                "\"code\":\"" + this.getCode() +
                "\", \"desc\":\"" + this.getDesc() + "\"" + "}";
    }


}
