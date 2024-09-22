package com.app.bookstore.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customerServiceAPI () {
        return new OpenAPI()
                .info(new Info()
                        .title("Product and Customer management API docs")
                        .description("RESTful API for Customer and Product services")
                        .version("1.0.0"));
    }

}
