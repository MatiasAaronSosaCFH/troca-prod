package com.c174.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorssConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry){

        registry.addMapping("/**")
                .allowedOriginPatterns("**")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

}
