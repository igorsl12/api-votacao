package com.igor.bbb_votacao; // Verifique se o seu pacote é este!

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Mapeia /images/** para a pasta uploads/ na raiz do projeto
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:./uploads/");
    }
}