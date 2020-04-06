package cn.zhiu.framework.restful.api.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * The type Abstract base controller.
 *
 * @author zhuzz
 * @time 2019 /04/03 15:11:10
 */
public abstract class AbstractBaseController implements Serializable {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static final String SUCCESS = "SUCCESS";

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));//true:允许输入空值，false:不能为空值
    }


    /**
     * 获得当前请求对象
     *
     * @return
     */
    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 添加HttpResponse header
     *
     * @param name
     * @param value
     */
    protected void addHeader(String name, String value) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.addHeader(name, value);
    }

    /**
     * 设置HttpResponse header
     *
     * @param name
     * @param value
     */
    protected void setHeader(String name, String value) {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        response.setHeader(name, value);
    }

    protected String getHeader(String name) {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader(name);
    }

}
