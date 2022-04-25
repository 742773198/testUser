package com.zlt.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CharsetFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("Filter初始化：init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if ( "get".equalsIgnoreCase( request.getMethod() ) ){
            request = new MyRequest(request);
        }else if ( "post".equalsIgnoreCase( request.getMethod() )){
            request.setCharacterEncoding("UTF-8");
        }
        //放行：方法之前是目标方法之前执行； 方法之后是目标方法之后执行； 不写这个方法目标方法就不会执行
        filterChain.doFilter(request,servletResponse);
        System.out.println("方法之后：after");
    }

    @Override
    public void destroy() {
        System.out.println("过滤结束：destroy");
    }
}
