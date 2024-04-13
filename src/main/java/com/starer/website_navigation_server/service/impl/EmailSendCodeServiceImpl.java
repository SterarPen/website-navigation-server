package com.starer.website_navigation_server.service.impl;

import com.starer.website_navigation_server.service.ISendCodeService;
import com.starer.website_navigation_server.util.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Properties;

@Service
public class EmailSendCodeServiceImpl implements ISendCodeService {

    private final JavaMailSender javaMailSender;
    private final Properties properties;

    @Autowired
    public EmailSendCodeServiceImpl(JavaMailSender javaMailSender, Properties properties) {
        this.javaMailSender = javaMailSender;
        this.properties = properties;
    }

    @Override
    public ServiceResult<HashMap<String, String>> sendCode(String receiver, String code) {
        if(receiver == null || code == null) {
            return ServiceResult.createFactory(
                    Integer.parseInt(properties.getProperty("FAILURE.CODE")),
                    properties.getProperty("FAILURE.CODE.METHOD_PROPERTIES_IS_NULL")
            );
        }

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("2482723192@qq.com");
        simpleMailMessage.setTo(receiver);
        simpleMailMessage.setSubject("验证码");
        simpleMailMessage.setText(code);

        javaMailSender.send(simpleMailMessage);

        HashMap<String, String> resultData = new HashMap<>();
        resultData.put(receiver, code);
        return ServiceResult.createFactory(
                Integer.parseInt(properties.getProperty("SUCCESS.CODE")),
                properties.getProperty("SUCCESS.CODE.SEND"),
                resultData
        );
    }
}
