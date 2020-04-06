package cn.zhiu.framework.base.api.core.annotation.request;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Objects;

public class HandlerMethodBodyArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.hasParameterAnnotation(RequestBodyParam.class);
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {

        RequestBodyParam parameterAnnotation = methodParameter.getParameterAnnotation(RequestBodyParam.class);

        String name = parameterAnnotation.name();

        JSONObject requestBody = getRequestBody(nativeWebRequest);

        if (StringUtils.isEmpty(name)) {
            name = methodParameter.getParameterName();
        }

        Type parameterType = methodParameter.getGenericParameterType();

        Object val = null;
        if (Objects.nonNull(requestBody)) {
            val = requestBody.getObject(name, parameterType);
        }

        if (Objects.isNull(val) && parameterAnnotation.required()) {
            throw new IllegalArgumentException(String.format("required param %s is not present", name));
        }

        return val;

    }

    private JSONObject getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        String jsonBody = (String) servletRequest.getAttribute(JSON_REQUEST_BODY);
        if (jsonBody == null) {
            try {
                jsonBody = IOUtils.toString(servletRequest.getInputStream());
                servletRequest.setAttribute(JSON_REQUEST_BODY, jsonBody);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return JSON.parseObject(jsonBody);

    }

    public static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";
}
