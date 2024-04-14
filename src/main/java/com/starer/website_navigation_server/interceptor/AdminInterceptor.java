package com.starer.website_navigation_server.interceptor;

import com.starer.website_navigation_server.util.JWTUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取Authorization请求头，该请求头是一个JWT格式的Token
        String token = request.getHeader("Authorization");
        // 如果不存在Authorization，直接返回
        if(token == null) {
            return false;
        }

        Jws<Claims> claimsJws = JWTUtil.parseClaim(token);
        String s = claimsJws.getPayload().get("id", String.class);
        return s.length() == 12 && s.startsWith("1");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
