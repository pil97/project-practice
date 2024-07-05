package com.docmall.basic;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@MapperScan(basePackages = ("com.docmall.basic.**"))
// @SpringBootApplication(exclude = SecurityAutoConfiguration.class) 이걸 사용하면 스프링 security 라이브러리 적용 안됨 -> login 페이지 안나옴 
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DocmallApplication {

	public static void main(String[] args) {
		SpringApplication.run(DocmallApplication.class, args);
	}

}
