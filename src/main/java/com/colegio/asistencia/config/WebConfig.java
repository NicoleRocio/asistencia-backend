package com.colegio.asistencia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Esto dice: "Cuando alguien pida una URL que empiece con /images/..."
        registry.addResourceHandler("/images/**")
                // "... búscalo en esta carpeta de mi disco duro"
                .addResourceLocations("file:C:/sist_escolar/uploads/");
    }
}