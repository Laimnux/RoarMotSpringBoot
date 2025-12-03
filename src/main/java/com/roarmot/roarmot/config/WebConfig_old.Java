package com.roarmot.roarmot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;

/**
 * Configuración para mapear rutas URL a recursos estáticos del sistema de archivos.
 * Esto es necesario para servir:
 * 1. Los archivos estáticos internos (CSS, JS, iconos) desde el classpath.
 * 2. Los archivos subidos por el usuario (imágenes de productos) desde la ruta física (file.upload-dir).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class); 

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        logger.info("🔍 [WEBCONFIG] Método ejecutándose...");
        logger.info("🔍 [WEBCONFIG] uploadDir = {}", uploadDir);

        // 1. Recursos estáticos
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");

        // 2. Imágenes de productos
        registry.addResourceHandler("/products/**")
                .addResourceLocations("file:" + uploadDir + "/products/");

        // 3. Imágenes de motos (NUEVO) - ¡TE FALTA ESTA LÍNEA!
        File rutaBase = new File("data/uploads/motos");
        String rutaMotos = rutaBase.getAbsolutePath() + File.separator;
        registry.addResourceHandler("/motos/**")  // ← ¡FALTABA ESTO!
                .addResourceLocations("file:" + rutaMotos.replace("\\", "/"));

        // 4. Imágenes de perfil
        registry.addResourceHandler("/perfiles/**")
                .addResourceLocations("file:" + uploadDir + "/perfiles/");

        logger.info("🔍 [WEBCONFIG] Ruta ABSOLUTA motos: {}", rutaMotos);
        logger.info("🔍 [WEBCONFIG] Ruta formateada: {}", rutaMotos.replace("\\", "/"));
    }
}