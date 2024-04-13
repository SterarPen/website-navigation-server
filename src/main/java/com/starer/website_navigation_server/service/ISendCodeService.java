package com.starer.website_navigation_server.service;

import com.starer.website_navigation_server.util.ServiceResult;

import java.util.HashMap;

public interface ISendCodeService {

    ServiceResult<HashMap<String, String>> sendCode(String receiver, String code);
}
