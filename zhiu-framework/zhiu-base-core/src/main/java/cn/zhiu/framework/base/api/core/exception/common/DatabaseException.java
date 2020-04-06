package cn.zhiu.framework.base.api.core.exception.common;


import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;
import cn.zhiu.framework.base.api.core.exception.BaseApiException;

@ExceptionCode(code = "BA0003", desc = "数据库错误")
public class DatabaseException extends BaseApiException {

    private static final long serialVersionUID = -3820652565565814774L;

    public DatabaseException() {
        super();
    }

    public DatabaseException(Throwable cause) {
        super(cause);
    }

    public DatabaseException(String message) {
        super(message);
    }
}
