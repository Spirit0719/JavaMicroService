package cn.zhiu.framework.restful.api.core.exception.user;


import cn.zhiu.framework.base.api.core.annotation.exception.ExceptionCode;
import cn.zhiu.framework.base.api.core.exception.RestfulApiException;

/**
 * The type User not found exception.
 *
 * @author zhuzz
 * @time 2019 /04/03 15:27:40
 */
@ExceptionCode(code = "CU0001", desc = "用户信息未找到")
public class UserNotFoundException extends RestfulApiException {
    private static final long serialVersionUID = 8247831302664346130L;

}
