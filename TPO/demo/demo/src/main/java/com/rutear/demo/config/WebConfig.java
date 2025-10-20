package com.rutear.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override public void addCorsMappings(CorsRegistry r) {
    r.addMapping("/**")
     .allowedOrigins("*")     // o restringí a tu URL de Codespaces/Vite
     .allowedMethods("GET","POST","PUT","DELETE","PATCH","OPTIONS");
  }
}
