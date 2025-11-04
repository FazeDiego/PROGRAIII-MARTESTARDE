package com.rutear.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.http.CacheControl;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override public void addCorsMappings(CorsRegistry r) {
    r.addMapping("/**")
     .allowedOrigins("*")     // o restringí a tu URL de Codespaces/Vite
     .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // Desactivar caché para archivos estáticos en desarrollo
    registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCacheControl(CacheControl.noCache().mustRevalidate());
  }
}
