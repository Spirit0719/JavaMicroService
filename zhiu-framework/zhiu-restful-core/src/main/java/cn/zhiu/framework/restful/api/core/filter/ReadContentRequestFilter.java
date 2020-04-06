package cn.zhiu.framework.restful.api.core.filter;

import com.google.common.collect.ImmutableSet;
import com.google.common.net.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Set;

/**
 * The type Read content request filter.
 *
 * @author zhuzz
 * @time 2019 /04/03 14:35:48
 */
public class ReadContentRequestFilter implements Filter {
    /**
     * The Logger.
     */
    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String INTERCEPT_CONTENT_TYPE_SET = "INTERCEPT_CONTENT_TYPE_SET";

    private static final String COMMA_SYMBOL = ",";

    //此过滤器支持的请求类型
    private static Set<String> SUPPORT_CONTENT_TYPE_SET;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String contentTypeSet = filterConfig.getInitParameter(INTERCEPT_CONTENT_TYPE_SET);

        logger.info("读配置，初始化 SUPPORT_CONTENT_TYPE_MAP={}", contentTypeSet);
        if (!StringUtils.isEmpty(contentTypeSet)) {
            SUPPORT_CONTENT_TYPE_SET = ImmutableSet.copyOf(StringUtils.split(contentTypeSet, COMMA_SYMBOL));
        }

    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String contentType = httpRequest.getHeader(HttpHeaders.CONTENT_TYPE);
        if (StringUtils.isNotEmpty(contentType) && contentType.contains(";")) {
            contentType = contentType.split(";")[0].trim();
        }
        if (StringUtils.isNotEmpty(contentType)
                && SUPPORT_CONTENT_TYPE_SET != null
                && SUPPORT_CONTENT_TYPE_SET.contains(contentType.toLowerCase())) {
            ReadContentHttpServletRequestWrapper requestWrapper = new ReadContentHttpServletRequestWrapper(httpRequest);
            chain.doFilter(requestWrapper, response);
        } else {
            chain.doFilter(request, response);
        }

    }

    @Override
    public void destroy() {
        SUPPORT_CONTENT_TYPE_SET = null;
    }
}
