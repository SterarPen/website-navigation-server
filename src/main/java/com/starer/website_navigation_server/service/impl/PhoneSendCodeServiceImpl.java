package com.starer.website_navigation_server.service.impl;

import com.starer.website_navigation_server.service.ISendCodeService;
import com.starer.website_navigation_server.util.ServiceResult;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class PhoneSendCodeServiceImpl implements ISendCodeService {
    @Override
    public ServiceResult<HashMap<String, String>> sendCode(String receiver, String code) {
        return null;
    }
}
