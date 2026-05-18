package com.pratap.hotel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Explicit mapping for Images folder on classpath
        registry.addResourceHandler("/Images/**")
                .addResourceLocations("classpath:/Images/");

        // Keep defaults for other static locations if needed
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}

