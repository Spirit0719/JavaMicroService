package cn.zhiu.framework.restful.api.core.exception;

import cn.zhiu.framework.base.api.core.exception.BaseApiException;
import cn.zhiu.framework.base.api.core.exception.RestfulApiException;
import com.alibaba.fastjson.JSON;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * The type Feign exception error decoder.
 *
 * @author zhuzz
 * @time 2019 /04/14 18:37:06
 */
@Configuration
public class FeignExceptionErrorDecoder implements ErrorDecoder {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Exception decode(String s, Response response) {

        try {
            String[] apiMethod = s.split("#");
            String service = apiMethod[0];
            String method = apiMethod[1];
            if (response.body() != null) {
                String body = Util.toString(response.body().asReader());
                ExceptionInfo exceptionInfo = JSON.parseObject(body, ExceptionInfo.class);
                String message = exceptionInfo.getMessage();
                int lastIndexOf = message.lastIndexOf("};");
                String errorMsg = message;
                if (lastIndexOf != -1) {
                    errorMsg = message.substring(0, message.lastIndexOf("};")) + "}";
                }
                logger.error(message);
                try {
                    BaseApiException exception = JSON.parseObject(errorMsg, BaseApiException.class);
                    if (Objects.nonNull(exception)) {
                        exception.setInterfaceName(service);
                        exception.setMethodName(method);
                        return exception;
                    }
                } catch (Exception ex) {
                }
                return new ErrorDecoder.Default().decode(s, response);
            }
        } catch (Exception e) {
            return new RestfulApiException(e.getMessage());
        }
        return new RestfulApiException();
    }

}

