package com.artinus.api;

import com.artinus.api.global.client.dto.CsrngProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication(scanBasePackages = "com.artinus")
@EntityScan(basePackages = "com.artinus")
@EnableJpaRepositories(basePackages = "com.artinus")
@EnableConfigurationProperties(CsrngProperties.class)
public class ApiApplication extends SpringBootServletInitializer implements WebApplicationInitializer {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
