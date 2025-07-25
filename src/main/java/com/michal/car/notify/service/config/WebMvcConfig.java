package com.michal.car.notify.service.config;

import com.michal.car.notify.service.interceptor.RestCallLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Michal Remis
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final RestCallLoggingInterceptor restCallLoggingInterceptor;

    public WebMvcConfig(final RestCallLoggingInterceptor restCallLoggingInterceptor) {
        this.restCallLoggingInterceptor = restCallLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(restCallLoggingInterceptor);
    }
}