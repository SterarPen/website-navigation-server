package com.starer.website_navigation_server.util;

import com.starer.website_navigation_server.pojo.dto.TokenInformation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Properties;

@Component
public class IdentifyUtil {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    @Qualifier("handleResultProperties")
    private Properties properties;

    public ServiceResult<String> identifyToken(String token) {
        ServiceResult<String> serviceResult;
        if(token == null) {
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.TOKEN")),
                    properties.getProperty("FAILURE.TOKEN.IS_EMPTY")
            );
            return serviceResult;
        }

        Jws<Claims> claimsJws = JWTUtil.parseClaim(token);
        String account = claimsJws.getPayload().get("id", String.class);
        String ip = claimsJws.getPayload().get("ip", String.class);
        String device = claimsJws.getPayload().get("device", String.class);
        Date expiration = claimsJws.getPayload().getExpiration();

        String redisToken = redisUtil.getString(account);
        if(account == null || ip == null || device == null ||
                redisToken == null || !redisToken.equals(token)) {
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.TOKEN")),
                    properties.getProperty("FAILURE.TOKEN.ERROR")
            );
            return serviceResult;
        }
        if(expiration.before(new Date())) {
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.TOKEN")),
                    properties.getProperty("FAILURE.TOKEN.EXPIRE")
            );
            return serviceResult;
        }

        // Token离过期时间在8分钟内，进行续期
        Date currentDate = new Date(System.currentTimeMillis());
        if(expiration.getTime() < currentDate.getTime() + 8*60*1000) {
            String newToken = JWTUtil.genAccessToken(account, currentDate, 3600L);
            redisUtil.setString(account, newToken);
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("SUCCESS.TOKEN")),
                    properties.getProperty("SUCCESS.TOKEN.ALL"),
                    newToken
            );
            return serviceResult;
        }
        serviceResult = ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.TOKEN")),
                properties.getProperty("SUCCESS.TOKEN.ALL"),
                token
        );
        return serviceResult;
    }

    public ServiceResult<String> identifyToken(String token, String ip, String device) {
        ServiceResult<String> serviceResult = identifyToken(token);
        if(!serviceResult.isSuccess()) {
            return serviceResult;
        }

        Jws<Claims> claimsJws = JWTUtil.parseClaim(token);
        String tokenIp = claimsJws.getPayload().get("ip", String.class);
        String tokenDevice = claimsJws.getPayload().get("device", String.class);

        System.out.println(tokenIp);
        System.out.println(tokenDevice);
        if(!tokenIp.equals(ip) || !tokenDevice.equals(device)) {
            serviceResult = ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.TOKEN")),
                    properties.getProperty("FAILURE.TOKEN.ERROR")
            );
            return serviceResult;
        }
        return serviceResult;
    }
}
