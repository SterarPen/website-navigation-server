package com.starer.website_navigation_server.filter;

import com.starer.website_navigation_server.util.IdentifyUtil;
import com.starer.website_navigation_server.util.ServiceResult;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
//@WebFilter(filterName = "permissionFilter", urlPatterns = {"/admin/**"})
public class PermissionFilter implements Filter {

    private final IdentifyUtil identifyUtil;

//    @Autowired
    public PermissionFilter(IdentifyUtil identifyUtil) {
        this.identifyUtil = identifyUtil;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

//        if("/index".equals(request.getRequestURI())) {
//            filterChain.doFilter(servletRequest,servletResponse);
//            return;
//        }

        /*
            检查是否存在登录信息
         */
        // 获取Authorization请求头，该请求头是一个JWT格式的Token
        String token = request.getHeader("Authorization");
        //如果不存在Authorization，直接返回
        if(token == null) {
            response.sendRedirect("/index");
            return;
        }

        ServiceResult<String> serviceResult = identifyUtil.identifyToken(token,
                request.getRemoteAddr(), request.getHeader("User-Agent"));
        if(!serviceResult.isSuccess()) {
            response.sendRedirect("/index");
            return;
        }
        if(!serviceResult.getData().equals(token)) {
            response.setHeader("Authorization", serviceResult.getData());
        }

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
