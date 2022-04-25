package com.zlt.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter(value = "/*",initParams = @WebInitParam(name="uris",value = "/user/update,/user/updateDo"))
public class LoginFilter implements Filter {

    private List<String> uris;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String s = filterConfig.getInitParameter("uris");  //获取注解上的属性uris字符串
        uris = Arrays.asList(s.split(","));
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //获取当前执行的路径（功能名）
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String requestURI = request.getRequestURI(); //获取URL，/testUser/user/update
        System.out.println("requestURI:"+requestURI);
        requestURI = requestURI.substring( request.getContextPath().length() );  // request.getContextPath()  为/testUser
        System.out.println("request.getContextPath() 为：" + request.getContextPath() );
        //判断是否放行（进行过滤）
        if(uris.contains(requestURI)){
            Object cur_user = request.getSession().getAttribute("CUR_USER");//判断是否登陆
            if(cur_user == null){
            response.sendRedirect("/testUser/user/login"); //未登录就让你登录
            return;
            }
        }
        filterChain.doFilter(request,response);  //执行过滤器
    }

    @Override
    public void destroy() {
        System.out.println("destroy");
    }
}
