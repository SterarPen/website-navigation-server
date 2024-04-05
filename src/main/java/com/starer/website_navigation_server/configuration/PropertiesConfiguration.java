package com.starer.website_navigation_server.configuration;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

@SpringBootConfiguration
public class PropertiesConfiguration {

    @Bean
    public Properties handleResultProperties() {
        String path = "F:\\Workspace\\IdeaProjects\\website-navigation-server\\src\\main\\resources\\result-cn.properties";
        File file = new File(path);
        try(FileReader fileReader = new FileReader(file)) {
            Properties properties = new Properties();
            properties.load(fileReader);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
