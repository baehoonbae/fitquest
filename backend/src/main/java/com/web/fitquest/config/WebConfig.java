package com.web.fitquest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:" + uploadPath);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/**")  // 모든 경로에 대해 CORS 설정 적용
                .allowedOrigins(
                    "http://localhost:5173",     // Vue 개발 서버
                    "http://localhost:5174",     // 예비 포트
                    "http://localhost:3000"      // 추가 개발 서버 포트
                )
                .allowedMethods(
                    "GET",
                    "POST", 
                    "PUT", 
                    "DELETE", 
                    "PATCH", 
                    "OPTIONS"
                )
                .allowedHeaders("*")            // 모든 헤더 허용
                .exposedHeaders(
                    "Authorization",
                    "Content-Type",
                    "X-Total-Count",
                    "Access-Control-Allow-Origin",
                    "Access-Control-Allow-Credentials"
                )
                .allowCredentials(true)         // 자격 증명 허용
                .maxAge(3600);                  // preflight 요청 결과를 1시간 동안 캐시

        // fitquest 전용 엔드포인트에 대한 추가 설정
        registry
            .addMapping("/fitquest/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}