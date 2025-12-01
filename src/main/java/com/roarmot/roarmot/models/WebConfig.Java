package com.roarmot.roarmot.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Configuración para mapear rutas URL a recursos estáticos del sistema de archivos.
 * Esto es necesario para servir:
 * 1. Los archivos estáticos internos (CSS, JS, iconos) desde el classpath.
 * 2. Los archivos subidos por el usuario (imágenes de productos) desde la ruta física (file.upload-dir).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Inyecta la ruta física definida en application.properties (data/uploads/products)
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 1. Configuración de Recursos Estáticos Internos (src/main/resources/static)
        // Mapea la raíz de la aplicación (/**) a la carpeta 'static'. 
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");


        // 2. Configuración para los archivos subidos (Imágenes de productos)
        // Mapea la URL '/products/**' a la carpeta 'data/uploads/products' en el sistema de archivos.
        // EJ: http://localhost:8080/products/a0a1bb90a-361c-4a0b-a236-957e8b1ddab8.png
        // IMPORTANTE: El prefijo 'file:' es obligatorio para rutas del sistema de archivos.
        // El 'uploadDir' debe terminar en '/' para que el mapeo funcione correctamente.
        // La ruta 'file:data/uploads/products/' asume que la carpeta 'data' está al nivel del 'src'.
        
        registry.addResourceHandler("/products/**")
                .addResourceLocations("file:" + uploadDir + "/"); // Usa la variable inyectada para mayor flexibilidad

        // Si la inyección del valor no funciona por alguna razón, puedes usar la ruta codificada (menos flexible, pero a veces útil para debug):
        // registry.addResourceHandler("/products/**")
        //         .addResourceLocations("file:data/uploads/products/");
    }
}