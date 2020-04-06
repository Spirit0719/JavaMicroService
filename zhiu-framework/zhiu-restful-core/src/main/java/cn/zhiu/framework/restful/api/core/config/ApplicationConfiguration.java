package cn.zhiu.framework.restful.api.core.config;

import cn.zhiu.framework.restful.api.core.filter.ReadContentRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * 多个应用配置
 *
 * @author zhuzz
 * @time 2019 /04/03 14:49:41
 */
@Configuration
public class ApplicationConfiguration {


    Logger logger = LoggerFactory.getLogger(this.getClass());

    /********************************************************
     * 注册字符集过滤器
     ********************************************************/
    @Bean
    public FilterRegistrationBean characterEncodingFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        registrationBean.setFilter(characterEncodingFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }


    /********************************************************
     * 注册预读过滤器
     ********************************************************/
    @Bean
    public FilterRegistrationBean readContentRequestFilterRegistrationBean() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        ReadContentRequestFilter readContentRequestFilter = new ReadContentRequestFilter();
        //过滤器关注的请求内容类型
        registrationBean.addInitParameter(ReadContentRequestFilter.INTERCEPT_CONTENT_TYPE_SET, "application/json,application/xml,application/x-www-form-urlencoded,multipart/form-data");
        registrationBean.setFilter(readContentRequestFilter);
        registrationBean.addUrlPatterns("/api/*");
        return registrationBean;
    }

    @Bean
    public ServletRegistrationBean apiDispatcherRegistration(DispatcherServlet dispatcherServlet) {

        AnnotationConfigWebApplicationContext applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ApiWebConfiguration.class);
        // 针对Controller的切面需要单独在当前的ApplicationContext中注册
        applicationContext.register(AspectJConfiguration.class);
        dispatcherServlet.setApplicationContext(applicationContext);
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(dispatcherServlet);
        servletRegistrationBean.addUrlMappings("/api/*");
        return servletRegistrationBean;
    }

    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setMaxUploadSize(104857600);
        resolver.setMaxInMemorySize(4096);
        return resolver;
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*"); // 1
        corsConfiguration.addAllowedHeader("*"); // 2
        corsConfiguration.addAllowedMethod("*"); // 3
        corsConfiguration.setMaxAge(3600L);
        return corsConfiguration;
    }

    @Bean
    @ConditionalOnExpression("${corsfilter.enable:false}")
    public CorsFilter corsFilter() {
        logger.error("******** set cors filter");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig()); // 4
        return new CorsFilter(source);
    }

}
