package com.wpf.conf;

import com.wpf.filter.RequestFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class FilterConfig extends WebMvcConfigurerAdapter {


    /**
     * 初始化过滤器
     *
     * @return
     * @Bean 注解必加，否则过滤器失效
     */
    @Bean
    public FilterRegistrationBean indexFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new RequestFilter());
        registration.addUrlPatterns("/*");
        /**
         * 再有一个过滤器的话，可以设置成 registration.setOrder(Integer.MAX_VALUE - 1)
         * spring boot 会按照order值的大小，从小到大的顺序来依次过滤
         */
        registration.setOrder(Integer.MAX_VALUE);

        return registration;
    }
}
