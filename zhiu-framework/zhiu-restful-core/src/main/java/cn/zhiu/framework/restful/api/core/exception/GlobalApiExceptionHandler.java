package cn.zhiu.framework.restful.api.core.exception;


import cn.zhiu.framework.base.api.core.exception.BaseApiException;
import cn.zhiu.framework.base.api.core.exception.RestfulApiException;
import cn.zhiu.framework.restful.api.core.bean.CommonBaseRestfulApiBean;
import cn.zhiu.framework.restful.api.core.bean.ParameterNotValidDetailItem;
import cn.zhiu.framework.restful.api.core.exception.common.ParameterNotPresentException;
import cn.zhiu.framework.restful.api.core.exception.common.ParameterNotValidException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.client.ClientException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * web应用层全局异常处理者
 * controller中的异常最终都上抛到此处理器中
 * 此处理器应当按照客户端请求响应规范返回http status,等异常信息
 */
@ControllerAdvice(annotations = RestController.class)
public class GlobalApiExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalApiExceptionHandler.class);
    public static final String REQUEST_CONTENT_BYTES = "REQUEST_CONTENT_BYTES";

    private static StringBuilder stringBufferFormat = new StringBuilder();

    static {
        //组织日志内容
        stringBufferFormat
                .append("接口调用出现异常")
                .append("\n\nERROR")
                .append("\n*******************************")
                .append("\n[Headers]")
                .append("\n%s")
                .append("\n[ClientIP]")
                .append("\n%s")
                .append("\n[ErrorCode]")
                .append("\n%s")
                .append("\n[ErrorMessage]")
                .append("\n%s")
                .append("\n[RequestParams]")
                .append("\n%s")
                .append("\n[RequestBody]")
                .append("\n%s")
                .append("\n[ErrorDetail]")
                .append("\n%s")
                .append("\n[ExceptionStackTrace]")
                .append("\n%s")
                .append("\nERROR END")
                .append("\n*******************************");
    }

    public GlobalApiExceptionHandler() {
        logger.info("异常处理器初始化成功");
    }

    @Autowired
    private ObjectMapper objectMapper;

    @ResponseBody
    @ExceptionHandler(Throwable.class)
    public CommonBaseRestfulApiBean allExceptionHandler(Throwable throwable) {
        // 进行异常分类和日志处理
        String errorCode = "RA9999";
        String errorMsg = "系统内部异常";
        String errorDetail = null;

        Throwable e = throwable;

        try {
            if (throwable instanceof MethodArgumentNotValidException) {
                // Valid 验证参数失败的异常
                BindingResult bindingResult = ((MethodArgumentNotValidException) throwable).getBindingResult();

                if (bindingResult != null && bindingResult.getErrorCount() > 0) {
                    List<ParameterNotValidDetailItem> detailItemList = new ArrayList<>();
                    for (ObjectError objectError : bindingResult.getAllErrors()) {
                        ParameterNotValidDetailItem detailItem = new ParameterNotValidDetailItem();
                        detailItem.setMessage(objectError.getDefaultMessage());

                        if (objectError instanceof FieldError) {
                            detailItem.setField(((FieldError) objectError).getField());
                        }

                        detailItemList.add(detailItem);
                    }

                    errorDetail = objectMapper.writeValueAsString(detailItemList);
                }

                throw new ParameterNotValidException(errorDetail);
            } else if (throwable instanceof MissingServletRequestParameterException) {
                MissingServletRequestParameterException ex = (MissingServletRequestParameterException) throwable;
                String parameterName = ex.getParameterName();
                throw new ParameterNotPresentException(String.format("%s参数是必填项!", parameterName));
            } else if (throwable instanceof MethodArgumentTypeMismatchException) {
                MethodArgumentTypeMismatchException ex = (MethodArgumentTypeMismatchException) throwable;
                String name = ex.getName();
                throw new ParameterNotValidException(String.format("%s参数类型不正确！", name));
            }
        } catch (Exception ex) {
            e = ex;
        }
        if (e.getCause() instanceof ClientException) {
            errorCode = "BS0001";
            errorMsg = "Unavailable service";
            errorDetail = e.getMessage();
        }

        if (e instanceof BaseApiException) {
            BaseApiException baseApiException = (BaseApiException) e;
            baseApiException.fillCodeDesc();
            errorCode = baseApiException.getCode();
            errorMsg = baseApiException.getDesc();
            errorDetail = String.format(" error : [%s]", baseApiException.getDesc());
        }

        // 如果是有定义的异常
        if (e instanceof RestfulApiException) {
            RestfulApiException exception = (RestfulApiException) e;
            if (StringUtils.isEmpty(exception.getCode())) {
                exception.fillCodeDesc();
                errorCode = exception.getCode();
                errorMsg = exception.getDesc();
                errorDetail = e.getMessage();
            } else {
                errorCode = exception.getCode();
                errorMsg = exception.getDesc();
                errorDetail = e.getMessage();
            }
        }

        ByteArrayOutputStream stackTraceOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(stackTraceOutputStream);
        e.printStackTrace(printStream);
        String exceptionStackTrace = new String(stackTraceOutputStream.toByteArray());

        printError(exceptionStackTrace, errorCode, errorMsg, errorDetail);


        CommonBaseRestfulApiBean commonBaseRestfulApiBean = new CommonBaseRestfulApiBean();
        commonBaseRestfulApiBean.setCode(errorCode);
        commonBaseRestfulApiBean.setErrorMsg(errorMsg);

        return commonBaseRestfulApiBean;


    }

    private void printError(String exceptionStackTrace, String errorCode, String errorMsg, String errorDetail) {
        StringBuilder stringBuffer = new StringBuilder();

        //得到请求内容
        String requestBody = StringUtils.EMPTY;
        ;
        HttpServletRequest request = getRequest();
        if (request.getAttribute(REQUEST_CONTENT_BYTES) != null) {
            requestBody = new String((byte[]) request.getAttribute(REQUEST_CONTENT_BYTES));
        }

        //获取请求表单
        stringBuffer
                .append("URI: ").append(request.getMethod()).append(StringUtils.SPACE)
                .append(request.getRequestURI()).append(StringUtils.LF)
                .append("Parameter: ");
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            stringBuffer
                    .append(name).append("=")
                    .append(request.getParameter(name)).append("&");
        }
        if (stringBuffer.length() > 0) {
            stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
        }
        String requestParams = stringBuffer.toString();
        stringBuffer.delete(0, stringBuffer.length());

        //获取headers
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            stringBuffer
                    .append(name).append(": ")
                    .append(request.getHeader(name)).append(StringUtils.LF);
        }
        String headers = stringBuffer.toString();
        stringBuffer.delete(0, stringBuffer.length());


        String log = String.format(stringBufferFormat.toString(),
                headers, getClientIP(),
                errorCode, errorMsg, requestParams, requestBody, errorDetail, exceptionStackTrace);

        logger.error(log);
    }

    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    protected String getClientIP() {
        HttpServletRequest request = getRequest();
        for (String headerName : httpProxyHeaderName) {
            String clientIP = request.getHeader(headerName);
            if (StringUtils.isNotBlank(clientIP)) {
                return clientIP;
            }
        }
        return request.getRemoteAddr();
    }

    protected static String[] httpProxyHeaderName = new String[]{
            "CDN-SRC-IP",
            "HTTP_CDN_SRC_IP",
            "CLIENTIP",
            "X-FORWARDED-FOR",
    };

}
