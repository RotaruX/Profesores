package es.alexrotaru.app_profesors.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir.examenes}")
    private String uploadDirExamenes;

    // Le dice a Spring: cuando alguien pida algo en /uploads/examenes/...,
    // buscalo en la carpeta fisica "uploads/examenes" del proyecto
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/examenes/**")
                .addResourceLocations("file:" + uploadDirExamenes + "/");
    }
}