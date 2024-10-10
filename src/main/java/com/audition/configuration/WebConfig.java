package com.audition.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ResponseHeaderInjector responseHeaderInjector;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register the ResponseHeaderInjector
        registry.addInterceptor(responseHeaderInjector);
    }
}
