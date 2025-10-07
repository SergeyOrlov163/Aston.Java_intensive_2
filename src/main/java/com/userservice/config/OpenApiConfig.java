package com.userservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API")
                        .description("RES API для управления пользователями с поддержкой HATEOAS")
                        .version("1.0.0")
                                .license(new License()
                                        .name("MIT License")
                                        .url("https://opensource.org/licenses/MIT")));
    }
}
