package cn.zhiu.framework.restful.api.core.exception.common;


import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;
import cn.zhiu.framework.base.api.core.exception.RestfulApiException;

@ExceptionCode(code = "NF0001", desc = "数据信息不存在")
public class NotFoundException extends RestfulApiException {

    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }

    private static final long serialVersionUID = -465235977075594428L;
}
