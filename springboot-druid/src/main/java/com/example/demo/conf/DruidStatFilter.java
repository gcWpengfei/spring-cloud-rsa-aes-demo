package com.example.demo.conf;


import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
  
import com.alibaba.druid.support.http.WebStatFilter;
  
/**
 * druid监控配置方式二 配置Filter  druid过滤器.
 * @author wpf
 *
 */
@WebFilter(filterName="druidWebStatFilter",urlPatterns="/*",
    initParams={
            @WebInitParam(name="exclusions",value="*.js,*.gif,*.jpg,*.bmp,*.png,*.css,*.ico,/druid/*")//忽略资源
     }
)
public class DruidStatFilter extends WebStatFilter{
  
}



