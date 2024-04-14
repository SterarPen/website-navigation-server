package com.starer.website_navigation_server.controller;

import com.starer.website_navigation_server.pojo.User;
import com.starer.website_navigation_server.pojo.dto.RegisterInformation;
import com.starer.website_navigation_server.service.IUserService;
import com.starer.website_navigation_server.util.JWTUtil;
import com.starer.website_navigation_server.util.ServiceResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Properties;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private IUserService userService;
    @Autowired
    private Properties properties;

    @GetMapping("/send-code/register")
    public ResponseEntity<?> register(@RequestParam("platform") String platform,
                                      @RequestParam("account") String account) {
        if (platform == null || account == null) {
            return ResponseEntity.badRequest().body(
                    properties.getProperty("FAILURE.CODE.METHOD_PROPERTIES_IS_NULL"));
        }

        ServiceResult<?> code = null;
        switch (platform) {
            case "email":
                code = userService.sendRegisterEmailCode(account.toLowerCase());
                break;
            case "phone":
                code = userService.sendRegisterPhoneCode(account);
                break;
            default:
                return ResponseEntity.badRequest().body(
                        properties.getProperty("FAILURE.CODE.PLATFORM_ERROR"));
        }

        if (code.isSuccess()) {
            return ResponseEntity.ok().body(
                    code.getMessage());
        } else {
            return ResponseEntity.badRequest().body(
                    code.getMessage());
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterInformation registerInformation) {
        if(registerInformation == null ||
                registerInformation.getPlatform() == null ||
                registerInformation.getAccount() == null ||
                registerInformation.getPassword() == null ||
                registerInformation.getCode() == null) {
            return ResponseEntity.badRequest().body(
                    properties.getProperty("FAILURE.REGISTER.INFORMATION_IS_NULL")
            );
        }


        ServiceResult<User> register = null;
        switch (registerInformation.getPlatform()) {
            case "email":
                registerInformation.setAccount(registerInformation.getAccount().toLowerCase());
                register = userService.registerByEmail(registerInformation);
                break;
            case "phone":
                register = userService.registerByPhone(registerInformation);
                break;
            default:
                return ResponseEntity.badRequest().body(
                        properties.getProperty("FAILURE.REGISTER.PLATFORM_ERROR"));
        }
        if(register != null && register.isSuccess()) {
            return ResponseEntity.ok().
                    body(register.getData().getUserId());
        } else {
            return ResponseEntity.badRequest().body(
                    properties.getProperty("FAILURE.REGISTER.ALL"));
        }
    }

    @GetMapping("/get-information")
    public ResponseEntity<?> getInformation(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");

        Jws<Claims> claimsJws = JWTUtil.parseClaim(authorization);
        String userId = claimsJws.getPayload().get("id", String.class);

        ServiceResult<User> information = userService.getInformation(userId);
        if(information.isSuccess()) {
            return ResponseEntity.ok().body(information.getData());
        } else {
            return ResponseEntity.badRequest().body(information.getMessage());
        }
    }

    @PutMapping("/update-information")
    public ResponseEntity<?> updateInformation(User user) {

        // 判断参数是否有效
        if(user == null) {
            return ResponseEntity.badRequest().body(properties.getProperty("FAILURE.USER.UPDATE"));
        }

        // 修改用户信息（普通信息，非验证类信息，例如不能为密码、手机号、邮箱）
        user.setPassword(null);
        user.setEmail(null);
        user.setPhone(null);
        ServiceResult<User> result = userService.updateInformation(user);

        // 返回修改结果
        return result.isSuccess() ?
                ResponseEntity.ok().body(result.getMessage()) :
                ResponseEntity.badRequest().body(result.getMessage());
    }

    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(HttpServletRequest request,
                                         @RequestParam("code") @Digits(integer = 6, fraction = 0) @Length(min = 6, max = 6) String code,
                                         @RequestParam("newEmail") @Email String newEmail,
                                         @RequestParam("newCode") @Digits(integer = 6, fraction = 0) @Length(min = 6, max = 6) String newCode) {

        // 若修改邮箱的必填字段为null，返回响应，提示错误
        if(code == null || newEmail == null || newCode == null) {
            return ResponseEntity.badRequest().body(
                    properties.getProperty("FAILURE.USER.METHOD_PROPERTIES_IS_NULL"));
        }


        // 通过Authorization请求头，获取用户ID
        String authorization = request.getHeader("Authorization");
        Jws<Claims> claimsJws = JWTUtil.parseClaim(authorization);
        String userId = claimsJws.getPayload().get("userId", String.class);

        // 更新邮箱
        ServiceResult<User> result = userService.updateEmail(userId, code, newEmail, newCode);

        if(result.isSuccess()) {
            return ResponseEntity.ok().body(result.getMessage());
        } else {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
    }

    @GetMapping("/send-code/update-email")
    public ResponseEntity<?> sendUpdateEmail(HttpServletRequest request) {
        // 通过Authorization请求头，获取用户ID
        String authorization = request.getHeader("Authorization");
        Jws<Claims> claimsJws = JWTUtil.parseClaim(authorization);
        String userId = claimsJws.getPayload().get("userId", String.class);

        ServiceResult<User> information = userService.getInformation(userId);
        if(!information.isSuccess()) {
            return ResponseEntity.badRequest().body(information.getMessage());
        }

        User user = information.getData();
        if(user == null) {
            return ResponseEntity.badRequest().body(properties.getProperty("FAILURE.USER.USER_IS_EMPTY"));
        }
        if(user.getEmail() == null) {
            return ResponseEntity.badRequest().body(properties.getProperty("FAILURE.USER.EMAIL_IS_EMPTY"));
        }

        ServiceResult<?> serviceResult = userService.sendCode(user.getEmail(), "email", "update-email");
        if(!serviceResult.isSuccess()) {
            return ResponseEntity.badRequest().body(serviceResult.getMessage());
        }

        return ResponseEntity.ok().body(serviceResult.getMessage());
    }

    @GetMapping("/send-code/update-email1")
    public ResponseEntity<?> sendUpdateEmail1(@RequestParam("newEmail") @Email String newEmail) {

        ServiceResult<Boolean> booleanServiceResult = userService.emptyUser(newEmail, null);
        if(!booleanServiceResult.isSuccess()) {
            return ResponseEntity.badRequest().body(booleanServiceResult.getMessage());
        }
        if(!booleanServiceResult.getData()) {
            return ResponseEntity.badRequest().body(properties.getProperty("FAILURE.USER.REPETITION_EMAIL"));
        }

        ServiceResult<?> serviceResult = userService.sendCode(newEmail, "email", "update-email");
        if(!serviceResult.isSuccess()) {
            return ResponseEntity.badRequest().body(serviceResult.getMessage());
        }

        return ResponseEntity.ok().body(serviceResult.getMessage());
    }

    @PutMapping("/update-phone")
    public ResponseEntity<?> updatePhone(HttpServletRequest request,
                                         @RequestParam("code") @Digits(integer = 6, fraction = 0) @Length(min = 6, max = 6) String code,
                                         @RequestParam("newPhone") @Pattern(regexp = "^((\\+|00)86)?1((3[\\d])|(4[5,6,7,9])|(5[0-3,5-9])|(6[5-7])|(7[0-8])|(8[\\d])|(9[1,8,9]))\\d{8}$") String newPhone,
                                         @RequestParam("newCode") @Digits(integer = 6, fraction = 0) @Length(min = 6, max = 6) String newCode) {

        // 若修改邮箱的必填字段为null，返回响应，提示错误
        if(code == null || newPhone == null || newCode == null) {
            return ResponseEntity.badRequest().body(
                    properties.getProperty("FAILURE.USER.METHOD_PROPERTIES_IS_NULL"));
        }


        // 通过Authorization请求头，获取用户ID
        String authorization = request.getHeader("Authorization");
        Jws<Claims> claimsJws = JWTUtil.parseClaim(authorization);
        String userId = claimsJws.getPayload().get("userId", String.class);

        // 更新手机号
        ServiceResult<User> result = userService.updatePhone(userId, code, newPhone, newCode);

        if(result.isSuccess()) {
            return ResponseEntity.ok().body(result.getMessage());
        } else {
            return ResponseEntity.badRequest().body(result.getMessage());
        }
    }

    @GetMapping("/send-code/update-phone")
    public ResponseEntity<?> sendUpdatePhone(HttpServletRequest request) {
        // 通过Authorization请求头，获取用户ID
        String authorization = request.getHeader("Authorization");
        Jws<Claims> claimsJws = JWTUtil.parseClaim(authorization);
        String userId = claimsJws.getPayload().get("userId", String.class);

        ServiceResult<User> information = userService.getInformation(userId);
        if(!information.isSuccess()) {
            return ResponseEntity.badRequest().body(information.getMessage());
        }

        User user = information.getData();
        if(user == null) {
            return ResponseEntity.badRequest().body(properties.getProperty("FAILURE.USER.USER_IS_EMPTY"));
        }
        if(user.getPhone() == null) {
            return ResponseEntity.badRequest().body(properties.getProperty("FAILURE.USER.PHONE_IS_EMPTY"));
        }

        ServiceResult<?> serviceResult = userService.sendCode(user.getPhone(), "phone", "update-phone");
        if(!serviceResult.isSuccess()) {
            return ResponseEntity.badRequest().body(serviceResult.getMessage());
        }

        return ResponseEntity.ok().body(serviceResult.getMessage());
    }

    @GetMapping("/send-code/update-phone1")
    public ResponseEntity<?> sendUpdatePhone1(@RequestParam("newPhone") @Pattern(regexp = "^((\\+|00)86)?1((3[\\d])|(4[5,6,7,9])|(5[0-3,5-9])|(6[5-7])|(7[0-8])|(8[\\d])|(9[1,8,9]))\\d{8}$") String newPhone) {

        ServiceResult<Boolean> booleanServiceResult = userService.emptyUser(null, newPhone);
        if(!booleanServiceResult.isSuccess()) {
            return ResponseEntity.badRequest().body(booleanServiceResult.getMessage());
        }
        if(!booleanServiceResult.getData()) {
            return ResponseEntity.badRequest().body(properties.getProperty("FAILURE.USER.REPETITION_PHONE"));
        }

        ServiceResult<?> serviceResult = userService.sendCode(newPhone, "phone", "update-phone");
        if(!serviceResult.isSuccess()) {
            return ResponseEntity.badRequest().body(serviceResult.getMessage());
        }

        return ResponseEntity.ok().body(serviceResult.getMessage());
    }
}
