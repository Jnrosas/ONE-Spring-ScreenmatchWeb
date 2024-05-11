package com.aluracursos.screenmatch.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Se configura Cors para que permita al frontend acceder a los recursos del back
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

   @Override
   public void addCorsMappings(CorsRegistry registry) {
      registry.addMapping("/**")
            .allowedOrigins("http://127.0.0.1:5500") //permite al puerto del front acceder (browser/inspect/console o inspect/network/header
            .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","HEAD","TRACE","CONNECT"); //da permiso para ejecutar esos metodos
   }
}
