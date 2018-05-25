package com.wpf.filter;


import com.wpf.conf.AMHttpServletRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/***
 * @Author:majun
 * @Desciption: 页面过滤器
 * @Date: 13:49 2018/1/15
 * @return
 */
public class RequestFilter implements Filter {



    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest http_req = (HttpServletRequest)request;
        AMHttpServletRequestWrapper amHttpServletRequestWrapper = new AMHttpServletRequestWrapper(http_req, http_req.getParameterMap());
        amHttpServletRequestWrapper.setParameter("data","111");
        amHttpServletRequestWrapper.setParameter("encryptkey","222");
        chain.doFilter(amHttpServletRequestWrapper, response);
    }

    @Override
    public void destroy() {

    }


}


