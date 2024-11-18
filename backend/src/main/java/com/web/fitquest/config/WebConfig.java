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
                .allowedOrigins("http://localhost:5173")  // 클라이언트 URL
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 허용할 HTTP 메서드
                .allowCredentials(true);  // 자격 증명 허용
    }
}
