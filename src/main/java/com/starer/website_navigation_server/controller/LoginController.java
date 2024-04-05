package com.starer.website_navigation_server.controller;

import com.starer.website_navigation_server.pojo.dto.LoginInformation;
import com.starer.website_navigation_server.pojo.dto.TokenInformation;
import com.starer.website_navigation_server.service.IAdminService;
import com.starer.website_navigation_server.util.IdentifyUtil;
import com.starer.website_navigation_server.util.ServiceResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IdentifyUtil identifyUtil;
    @Autowired
    private IAdminService adminService;

    @PostMapping("/admin1")
    public String loginAdmin1(@RequestParam("login_type") Byte loginType,
                             @RequestParam("account") String account,
                             @RequestParam("token") String token,
                             HttpServletRequest request,
                             HttpServletResponse response) {

        String authorization = request.getHeader("Authorization");
        String ip = request.getRemoteAddr();
        String device = request.getHeader("User-Agent");
        if (authorization != null) {
            ServiceResult<String> serviceResult = identifyUtil.identifyToken(authorization, ip, device);
            if(serviceResult.isSuccess()) {
                if(!authorization.equals(serviceResult.getData())) {
                    response.setHeader("Authorization", serviceResult.getData());
                }
                response.setHeader("Message", URLEncoder.encode(serviceResult.getMessage(), StandardCharsets.UTF_8));
                return "home";
            }
        }

        LoginInformation loginInformation = LoginInformation.createFactory(loginType, account, token);
        ServiceResult<TokenInformation> login = adminService.login(loginInformation, ip, device);
        if(login.isSuccess()) {
            response.setHeader("Message", URLEncoder.encode(login.getMessage(), StandardCharsets.UTF_8));
            response.setHeader("Authorization", login.getData().getToken());

            return "home";
        }
        response.setHeader("Message", URLEncoder.encode(login.getMessage(), StandardCharsets.UTF_8));
        return null;
    }

    @PostMapping("/admin")
    @ResponseBody
    public ResponseEntity<?> loginAdmin(HttpServletRequest request,
                                     HttpServletResponse response) throws IOException {
        Byte loginType = request.getParameter("login_type") != null ?
                Byte.parseByte(request.getParameter("login_type")) :
                null;
        String account = request.getParameter("account");
        String token = request.getParameter("token");
        String authorization = request.getHeader("Authorization");
        String ip = request.getRemoteAddr();
        String device = request.getHeader("User-Agent");
        if (authorization != null) {
            ServiceResult<String> serviceResult = identifyUtil.identifyToken(authorization, ip, device);
            if(serviceResult.isSuccess()) {
                if(!authorization.equals(serviceResult.getData())) {
                    response.setHeader("Authorization", serviceResult.getData());
                }
                response.setHeader("Message", URLEncoder.encode(serviceResult.getMessage(), StandardCharsets.UTF_8));
                return ResponseEntity.ok(serviceResult.getData());
            }
        }

        LoginInformation loginInformation = LoginInformation.createFactory(loginType, account, token);
        ServiceResult<TokenInformation> login = adminService.login(loginInformation, ip, device);
        if(login.isSuccess()) {
            response.setHeader("Message", URLEncoder.encode(login.getMessage(), StandardCharsets.UTF_8));
            response.setHeader("Authorization", login.getData().getToken());
            return ResponseEntity.ok(login.getData());
        }
        response.setHeader("Message", URLEncoder.encode(login.getMessage(), StandardCharsets.UTF_8));
        return ResponseEntity.badRequest().body(login.getData());
    }
}
