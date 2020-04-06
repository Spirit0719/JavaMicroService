package cn.zhiu.framework.restful.api.core.config;

import cn.zhiu.framework.base.api.core.util.MD5Util;
import cn.zhiu.framework.bean.core.enums.ClientType;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Configuration;

import static cn.zhiu.framework.base.api.core.constant.HeaderNameConstant.*;

@Configuration
@ConditionalOnExpression("${server.auth.enable:false}")
public class FeignConfiguration implements RequestInterceptor {

    @Value("${server.auth.salt}")
    private String serverAuthSalt;

    @Override
    public void apply(RequestTemplate template) {

        String signature = "prefix-" + System.currentTimeMillis();
        signature = MD5Util.encoderByMd5(signature);
        String clientType = ClientType.ZHIUWEB.toString();
        template.header(API_CLIENT_TYPE, clientType);
        template.header(API_CLIENT_SIGNATURE, signature);
        template.header(API_CLIENT_TOKEN, MD5Util.encoderByMd5(clientType + serverAuthSalt + signature));
    }

}
