package com.starer.website_navigation_server.service.impl;

import com.starer.website_navigation_server.dao.IAdminDao;
import com.starer.website_navigation_server.pojo.Admin;
import com.starer.website_navigation_server.pojo.dto.LoginInformation;
import com.starer.website_navigation_server.pojo.dto.RegisterInformation;
import com.starer.website_navigation_server.pojo.dto.TokenInformation;
import com.starer.website_navigation_server.service.IAdminService;
import com.starer.website_navigation_server.util.JWTUtil;
import com.starer.website_navigation_server.util.RedisUtil;
import com.starer.website_navigation_server.util.SendMessageUtil;
import com.starer.website_navigation_server.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Properties;

@Service
public class AdminServiceImpl implements IAdminService {

    private final IAdminDao adminDao;
    private final Properties properties;
    private final RedisUtil redisUtil;

    @Autowired
    public AdminServiceImpl(IAdminDao adminDao, Properties properties,
        RedisUtil redisUtil) {
        this.adminDao = adminDao;
        this.properties = properties;
        this.redisUtil = redisUtil;
    }

    @Override
    public ServiceResult<TokenInformation> login(LoginInformation loginInformation, String ip, String device) {
        ServiceResult<TokenInformation> serviceResult;

        if(loginInformation == null) {
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.LOGIN")),
                    properties.getProperty("FAILURE.LOGIN.ERROR_INFORMATION")
            );
            return serviceResult;
        }

        boolean loginSuccess = false;
        Admin admin = new Admin();
        switch (loginInformation.getLoginType()) {
            case LoginInformation.ID_AND_PASSWORD:
                admin.setAdminId(loginInformation.getAccount());
                admin.setPassword(loginInformation.getToken());
                break;
            case LoginInformation.PHONE_AND_PASSWORD:
                admin.setPhone(loginInformation.getAccount());
                admin.setPassword(loginInformation.getToken());
                break;
            case LoginInformation.EMAIL_AND_PASSWORD:
                admin.setEmail(loginInformation.getAccount());
                admin.setPassword(loginInformation.getToken());
                break;
            case LoginInformation.PHONE_AND_CODE:
                String string = redisUtil.getString(loginInformation.getAccount());
                if(string != null && string.equals(loginInformation.getToken())) {
                    loginSuccess = true;
                    admin.setPhone(loginInformation.getAccount());
                }
                break;
            case LoginInformation.EMAIL_AND_CODE:
                String str2 = redisUtil.getString(loginInformation.getAccount());
                if(str2 != null && str2.equals(loginInformation.getToken())) {
                    loginSuccess = true;
                    admin.setEmail(loginInformation.getAccount());
                }
                break;
            default:
                serviceResult = ServiceResult.createFactory(
                        Integer.parseInt(properties.getProperty("FAILURE.LOGIN")),
                        properties.getProperty("FAILURE.LOGIN.TYPE")
                );
                return serviceResult;
        }

        Admin[] select = adminDao.select(admin);
        if (select.length == 1) {
            Date date = new Date();
            String s = JWTUtil.genAccessToken(select[0].getAdminId(), ip,
                    device, date, 3600L);

            redisUtil.setString(select[0].getAdminId(), s);
            redisUtil.expire(select[0].getAdminId(), 3600);

            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("SUCCESS.LOGIN")),
                    properties.getProperty("SUCCESS.LOGIN.ALL"),
                    new TokenInformation(select[0].getAdminId(), s, ip, device, date, 3600L)
            );
            return serviceResult;
        } else if(select.length == 0) {
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.LOGIN")),
                    properties.getProperty("FAILURE.LOGIN.ID_OR_PASSWORD")
            );
            return serviceResult;
        } else {
            throw new RuntimeException("存在重复属性");
        }
    }

    @Override
    public ServiceResult<Admin> register(RegisterInformation registerInformation) {
        return null;
    }
}
