//package com.example.demo.conf;
//
//import com.alibaba.druid.filter.Filter;
//import com.alibaba.druid.pool.DruidDataSource;
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.alibaba.druid.support.http.WebStatFilter;
//import com.alibaba.druid.wall.WallConfig;
//import com.alibaba.druid.wall.WallFilter;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.context.annotation.Primary;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @Author:majun
// * @Description: 数据库连接池 配置
// * @Date:Created in 11:06 2018/2/28
// * @Modified By:
// */
//@Configuration
//public class DataSourcesConfig {
//
//    /**
//     * druid初始化
//     * @return
//     * @throws SQLException
//     */
//    @Primary //默认数据源
//    @Bean(name = "dataSource",destroyMethod = "close")
//    @ConfigurationProperties(prefix = "spring.datasource")
//    public DruidDataSource Construction() throws SQLException {
//
//        DruidDataSource datasource = new DruidDataSource();
//
//        // filter
//        List<Filter> filters = new ArrayList<Filter>();
//        WallFilter wallFilter = new WallFilter();
//        filters.add(wallFilter);
//        datasource.setProxyFilters(filters);
//
//        return datasource;
//
//    }
//
//    @Bean(name = "wallFilter")
//    @DependsOn("wallConfig")
//    public WallFilter wallFilter(WallConfig wallConfig){
//        WallFilter wallFilter = new WallFilter();
//        wallFilter.setConfig(wallConfig);
//        return wallFilter;
//    }
//
//    @Bean(name = "wallConfig")
//    public WallConfig wallConfig(){
//        WallConfig wallConfig = new WallConfig();
//        wallConfig.setMultiStatementAllow(true);//允许一次执行多条语句
//        wallConfig.setNoneBaseStatementAllow(true);//允许一次执行多条语句
//        return wallConfig;
//    }
//
//    /**
//     * druid监控
//     * 访问：http://localhost:8080/druid/login.html
//     * @return
//     */
//    @Bean
//    public ServletRegistrationBean druidServlet() {
//        ServletRegistrationBean reg = new ServletRegistrationBean();
//        reg.setServlet(new StatViewServlet());
//        reg.addUrlMappings("/druid/*");
//        //reg.addInitParameter("allow", "127.0.0.1");
//        //reg.addInitParameter("deny","");
//        reg.addInitParameter("loginUsername", "admin");
//        reg.addInitParameter("loginPassword", "admin");
//        return reg;
//    }
//
//    /**
//     * druid监控过滤
//     * @return
//     */
//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
//        filterRegistrationBean.setFilter(new WebStatFilter());
//        filterRegistrationBean.addUrlPatterns("/*");
//        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
//        return filterRegistrationBean;
//    }
//
//}
