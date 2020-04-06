package cn.zhiu.framework.base.api.core.exception;

import cn.zhiu.framework.base.api.core.util.ReflectionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


/**
 * The type Global universal api exception handler.
 *
 * @author zhuzz
 * @time 2019 /04/03 15:16:24
 */
@ControllerAdvice
public class GlobalUniversalApiExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalUniversalApiExceptionHandler.class);


    private static StringBuilder stringBufferFormat = new StringBuilder();

    private ObjectMapper objectMapper = new ObjectMapper();

    static {
        //组织日志内容
        stringBufferFormat
                .append("接口调用出现异常")
                .append("\n\nERROR")
                .append("\n*******************************")
                .append("\n[ErrorCode]")
                .append("\n%s")
                .append("\n[ErrorMessage]")
                .append("\n%s")
                .append("\n[ErrorDetail]")
                .append("\n%s")
                .append("\n[ExceptionStackTrace]")
                .append("\n%s")
                .append("\nERROR END")
                .append("\n*******************************\n");
    }

    private static String MODEL = "PU";


    @Autowired
    private HttpServletResponse response;


    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object exceptionErrorHandler(Exception e) throws Exception {
        BaseApiException baseApiException = new BaseApiException(e);
        baseApiException.fillCodeDesc();
        this.printError(baseApiException, baseApiException.getCode(), baseApiException.getDesc(), baseApiException.getMessage());
        String value = baseApiException.toString();
        ReflectionUtils.setValueByFieldName(e, "detailMessage", value);
        throw baseApiException;
    }

    private void printError(BaseApiException ex, String errorCode, String errorMsg, String errorDetail) {


        //得到异常栈内容
        ByteArrayOutputStream stackTraceOutputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(stackTraceOutputStream);
        ex.printStackTrace(printStream);
        String exceptionStackTrace = new String(stackTraceOutputStream.toByteArray());

        String log = String.format(stringBufferFormat.toString(),
                MODEL, errorCode, errorMsg, errorDetail, exceptionStackTrace);

        logger.error(log);
    }

}
