package cn.zhiu.framework.restful.api.core;

import cn.zhiu.framework.base.api.core.config.BaseServiceWebMvcConfig;
import cn.zhiu.framework.base.api.core.exception.GlobalUniversalApiExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.elasticsearch.ElasticSearchRestHealthIndicatorAutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

/**
 * 启动入口
 */

@SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class
        , org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class, JpaRepositoriesAutoConfiguration.class, ElasticsearchAutoConfiguration.class
        , ElasticsearchDataAutoConfiguration.class, GlobalUniversalApiExceptionHandler.class, BaseServiceWebMvcConfig.class})
@ComponentScan(value = {"cn.zhiu.framework.configuration", "cn.zhiu.restful.api", "cn.zhiu.framework.restful.api.core", "cn.zhiu.framework.base.api.core", "cn.zhiu.dispatcher"},
        excludeFilters = @ComponentScan.Filter(type = ASSIGNABLE_TYPE, classes = BaseServiceWebMvcConfig.class)
)
@EnableDiscoveryClient
@EnableFeignClients("cn.zhiu.base.api")
@ComponentScan("cn.zhiu.base.api")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, ElasticSearchRestHealthIndicatorAutoConfiguration.class})
public class Main {


    public static void main(String[] args) {

        SpringApplication.run(Main.class, args);

    }
}
