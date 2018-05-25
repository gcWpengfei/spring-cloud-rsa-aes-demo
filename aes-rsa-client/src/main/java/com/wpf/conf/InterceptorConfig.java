package com.wpf.conf;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.wpf.interceptor.ParamInterceptor;


/***
* @Author:majun
* @Desciption: 拦截器
* @Date: 13:53 2018/1/15
* @return 
*/
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ParamInterceptor()).addPathPatterns("/**");
    }
}
