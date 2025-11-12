package com.malvader.banco.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/auth/acesso-negado",
                        "/auth/verificar-cpf",
                        "/",
                        "/sobre",
                        "/error",
                        "/favicon.ico",
                        "/css/**", "/js/**", "/images/**", "/webjars/**",
                        "/clientes/**"   // <— TEMPORÁRIO
                );
    }


}

