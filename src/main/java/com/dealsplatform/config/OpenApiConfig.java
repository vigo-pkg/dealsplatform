package com.dealsplatform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Deals Platform API")
                .description("REST API для платформы заключения пари между сторонами")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Deals Platform Team")
                    .email("support@dealsplatform.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Локальная среда разработки"),
                new Server()
                    .url("https://api.dealsplatform.com")
                    .description("Продакшн сервер")
            ));
    }
}
