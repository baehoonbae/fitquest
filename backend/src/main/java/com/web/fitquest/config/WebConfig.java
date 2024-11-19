package com.web.fitquest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/fitquest/api/**")
                .allowedHeaders("*")
                .allowedOriginPatterns("http://localhost:[*]", "http://127.0.0.1:[*]", "http://70.12.50.63:[*]")
                .allowedMethods("GET", "POST", "PUT", "DELETE") 
                .allowCredentials(true); 
    }
}
