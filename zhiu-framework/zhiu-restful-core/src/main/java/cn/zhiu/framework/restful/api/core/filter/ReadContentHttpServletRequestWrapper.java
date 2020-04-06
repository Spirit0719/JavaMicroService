package cn.zhiu.framework.restful.api.core.filter;

import cn.zhiu.framework.base.api.core.util.ParamUtil;
import cn.zhiu.framework.restful.api.core.exception.common.ParameterNotValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * request内容预读包装器
 *
 * @author zhuzz
 * @time 2019 /04/03 14:37:32
 */
public class ReadContentHttpServletRequestWrapper extends HttpServletRequestWrapper {

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String REQUEST_CONTENT_BYTES = "REQUEST_CONTENT_BYTES";

    private ByteArrayInputStream byteArrayInputStream;

    public ReadContentHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        logger.error("params : " + request.getParameterMap());

        // region 增加非法参数校验
        if (!CollectionUtils.isEmpty(request.getParameterMap())) {
            request.getParameterMap().forEach((k, v) -> {
                if (Objects.isNull(v) || Objects.isNull(v[0]) || k.toLowerCase().indexOf("id") == -1 || !ParamUtil.isNumber(v[0])) {
                    return;
                }
                if (Long.valueOf(v[0]) < 1) {
                    throw new ParameterNotValidException();
                }
            });
        }
        // endregion
        
        BufferedInputStream bufferedInputStream = null;
        try {
            ServletInputStream originalInputStream = request.getInputStream();
            if (originalInputStream != null) {
                bufferedInputStream = new BufferedInputStream(originalInputStream);
                ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

                int len;
                int BUFFER_LEN = 2048;
                byte[] bytes = new byte[BUFFER_LEN];
                while ((len = bufferedInputStream.read(bytes, 0, BUFFER_LEN)) != -1) {
                    arrayOutputStream.write(bytes, 0, len);
                }

                byte[] contentBytes = arrayOutputStream.toByteArray();
                request.setAttribute(REQUEST_CONTENT_BYTES, contentBytes);
                byteArrayInputStream = new ByteArrayInputStream(contentBytes);
            } else {
                byteArrayInputStream = new ByteArrayInputStream(new byte[0]);
            }
        } catch (
                IOException ex) {
            logger.error("实例化HttpServletRequestReadAheadWrapper阶段，读取请求内容时发生异常!");
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException ex) {
                    logger.error("Error closing bufferedInputStream...", ex);
                }
            }

        }

    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener listener) {
            }

            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }


}
