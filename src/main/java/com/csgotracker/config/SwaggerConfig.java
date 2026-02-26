package com.csgotracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CS:GO Market Tracker API")
                        .version("1.0")
                        .description("REST API for tracking CS:GO skins prices with historical data and trends analysis")
                        .contact(new Contact()
                                .name("Matveykk")
                                .url("https://github.com/Matveykk/csgo-market-tracker")
                        )
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local server")
                ));
    }
}