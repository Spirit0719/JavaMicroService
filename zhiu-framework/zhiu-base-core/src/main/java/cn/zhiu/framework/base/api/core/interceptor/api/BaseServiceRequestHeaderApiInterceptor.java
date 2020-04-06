package cn.zhiu.framework.base.api.core.interceptor.api;

import cn.zhiu.framework.base.api.core.exception.common.ClientTypeNotSupportException;
import cn.zhiu.framework.base.api.core.exception.common.ParamValidException;
import cn.zhiu.framework.base.api.core.util.MD5Util;
import cn.zhiu.framework.bean.core.enums.ClientType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static cn.zhiu.framework.base.api.core.constant.HeaderNameConstant.*;

@Component
@ConditionalOnExpression("${server.auth.enable:false}")
public class BaseServiceRequestHeaderApiInterceptor extends BaseHandlerInterceptorAdapter {

    @Value("${server.auth.salt}")
    private String serverAuthSalt;

    private String actuator = "/actuator";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().indexOf(actuator) == -1) {
            String clientType = request.getHeader(API_CLIENT_TYPE);
            if (StringUtils.isBlank(clientType)) {
                throw new ClientTypeNotSupportException();
            }
            ClientType type = ClientType.valueOf(clientType);
            String signature = request.getHeader(API_CLIENT_SIGNATURE);
            String token = request.getHeader(API_CLIENT_TOKEN);
            if (StringUtils.isBlank(signature) || StringUtils.isBlank(token)) {
                throw new ParamValidException();
            }
            String md5Result = MD5Util.encoderByMd5(type.toString() + serverAuthSalt + signature);
            if (!md5Result.equals(token)) {
                throw new ParamValidException();
            }
        }
        return super.preHandle(request, response, handler);
    }
}
