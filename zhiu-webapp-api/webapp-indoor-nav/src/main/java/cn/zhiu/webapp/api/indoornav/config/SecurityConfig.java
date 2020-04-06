package cn.zhiu.webapp.api.indoornav.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configurable
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        System.out.println("------------------------------------SecurityConfig_configure-----------------------------------------");

        http.authorizeRequests().anyRequest().permitAll().and().logout().permitAll();
        http.csrf().disable();
    }

}