package com.starer.website_navigation_server.controller;

import com.starer.website_navigation_server.pojo.Admin;
import com.starer.website_navigation_server.pojo.dto.LoginInformation;
import com.starer.website_navigation_server.pojo.dto.TokenInformation;
import com.starer.website_navigation_server.service.IAdminService;
import com.starer.website_navigation_server.util.ServiceResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final IAdminService adminService;

    @Autowired
    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("login_type") Byte loginType,
                                   @RequestParam("account") String account,
                                   @RequestParam("token") String token,
                                   HttpServletRequest request) {
        LoginInformation loginInformation = LoginInformation.createFactory(loginType, account, token);
        String ip = request.getRemoteAddr();
        String device = request.getHeader("User-Agent");
        ServiceResult<TokenInformation> login = adminService.login(loginInformation, ip, device);
        if(login.isSuccess()) {
            return ResponseEntity.ok().header("Authorization", login.getData().getToken()).body(login.getData().getToken());
        }
        return ResponseEntity.badRequest().body(login.getMessage());
    }

//    @GetMapping("/home")
//    public String get() {
//        return "dadsad";
//    }

//    @GetMapping("/")
//    public String f(){
//        return "1";
//    }
}
