package com.starer.website_navigation_server.configuration;

import com.starer.website_navigation_server.filter.LoginFilter;
import com.starer.website_navigation_server.filter.PermissionFilter;
import com.starer.website_navigation_server.util.IdentifyUtil;
import com.starer.website_navigation_server.util.RedisUtil;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class FilterConfiguration {

    @Autowired
    private IdentifyUtil identifyUtil;

    @Bean
    public FilterRegistrationBean<PermissionFilter> getLoginFilter(IdentifyUtil identifyUtil) {
        FilterRegistrationBean<PermissionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PermissionFilter(identifyUtil));
        registration.addUrlPatterns("/admin/*");
        return registration;
    }
}
