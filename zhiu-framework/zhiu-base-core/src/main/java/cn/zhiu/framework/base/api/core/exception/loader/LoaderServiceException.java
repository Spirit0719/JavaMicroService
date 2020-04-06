package cn.zhiu.framework.base.api.core.exception.loader;

import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;
import cn.zhiu.framework.base.api.core.exception.BaseApiException;

/**
 * The type Loader service exception.
 *
 * @author zhuzz
 * @time 2019 /04/02 15:10:36
 */
@ExceptionCode(code = "BA0006", desc = "加载数据错误")
public class LoaderServiceException extends BaseApiException {

    public LoaderServiceException() {
        super();
    }

    public LoaderServiceException(String message) {
        super(message);
    }

    public LoaderServiceException(Throwable cause) {
        super(cause);
    }

    public LoaderServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
