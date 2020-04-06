package cn.zhiu.webapp.api.indoornav.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket customDocket() {
        System.out.println("------------------------------------SwaggerConfig_Docket-----------------------------------------");
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.zhiu.webapp.api.indoornav.controller")) // 扫描的包路径
                .build();
    }

    private ApiInfo apiInfo() {
        System.out.println("------------------------------------SwaggerConfig_apiInfo-----------------------------------------");

        return new ApiInfoBuilder()
                .title("indoor_nav ")//文档说明
                .version("1.0.0")//文档版本说明
                .termsOfServiceUrl("http://localhost:7036/indoornav/api/")
                .licenseUrl("http://localhost:7036/indoornav/api/")
                .build();

    }
}
