package com.igor.bbb_votacao;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Libera o acesso público à pasta "uploads" que vamos criar
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}