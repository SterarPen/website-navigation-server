package com.starer.website_navigation_server.controller;

import com.starer.website_navigation_server.util.IdentifyUtil;
import com.starer.website_navigation_server.util.ServiceResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @Autowired
    private IdentifyUtil identifyUtil;

    @GetMapping("/admin/")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        // 获取Authorization请求头，该请求头是一个JWT格式的Token
        String token = request.getHeader("Authorization");
        //如果不存在Authorization，直接返回
        if(token == null) {
            return "index";
        }

        ServiceResult<String> serviceResult = identifyUtil.identifyToken(token,
                request.getRemoteAddr(), request.getHeader("User-Agent"));
        if(!serviceResult.isSuccess()) {
            return "index";
        }
        if(!serviceResult.getData().equals(token)) {
            response.setHeader("Authorization", serviceResult.getData());
        }
        return "home";
    }

    @GetMapping("/index")
    public String index1(HttpServletRequest request, HttpServletResponse response) {
        // 获取Authorization请求头，该请求头是一个JWT格式的Token
        String token = request.getHeader("Authorization");
        //如果不存在Authorization，直接返回
        if(token == null) {
            return "index";
        }

        ServiceResult<String> serviceResult = identifyUtil.identifyToken(token,
                request.getRemoteAddr(), request.getHeader("User-Agent"));
        if(!serviceResult.isSuccess()) {
            return "index";
        }
        if(!serviceResult.getData().equals(token)) {
            response.setHeader("Authorization", serviceResult.getData());
        }
        return "home";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
