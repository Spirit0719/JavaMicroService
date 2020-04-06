package cn.zhiu.webapp.api.indoornav;

import cn.zhiu.framework.base.api.core.config.BaseServiceWebMvcConfig;
import cn.zhiu.framework.base.api.core.config.SecurityPermitAllConfig;
import cn.zhiu.framework.base.api.core.exception.GlobalUniversalApiExceptionHandler;
import cn.zhiu.framework.bean.core.dao.BaseRepositoryFactoryBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@EnableDiscoveryClient
@ComponentScan(value = {
        "cn.zhiu.framework.configuration",
        "cn.zhiu.webapp.api",
        "cn.zhiu.base.api",
        "cn.zhiu.framework.restful.api.core",
        "cn.zhiu.framework.base.api.core",
        "cn.zhiu.framework.bean.core.dao",
        "cn.zhiu.webapp.api.indoornav.controller"
},
        excludeFilters = @ComponentScan.Filter(
                type = ASSIGNABLE_TYPE,
                classes = {
                        BaseServiceWebMvcConfig.class,
                        GlobalUniversalApiExceptionHandler.class,
                        BaseServiceWebMvcConfig.class,
                        SecurityPermitAllConfig.class
                }
        )
)


@EnableJpaRepositories(repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class, basePackages = "cn.zhiu.webapp.api")
@ImportResource("classpath:META-INF/spring/*.xml")
@EnableFeignClients("cn.zhiu.base.api")
@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@EnableSwagger2
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
