package com.web.fitquest;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.web.fitquest.mapper")
public class FitquestBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitquestBackApplication.class, args);
	}

}