package com.starer.website_navigation_server.service;

import com.starer.website_navigation_server.pojo.Admin;
import com.starer.website_navigation_server.pojo.dto.LoginInformation;
import com.starer.website_navigation_server.pojo.dto.RegisterInformation;
import com.starer.website_navigation_server.pojo.dto.TokenInformation;
import com.starer.website_navigation_server.util.ServiceResult;

public interface IAdminService {

    ServiceResult<TokenInformation> login(LoginInformation loginInformation, String ip, String device);
    ServiceResult<Admin> register(RegisterInformation registerInformation);

}
