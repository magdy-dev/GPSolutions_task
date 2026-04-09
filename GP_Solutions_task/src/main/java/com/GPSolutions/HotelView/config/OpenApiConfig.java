package com.GPSolutions.HotelView.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI propertyViewOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Property View API")
                        .description("REST API for hotel management: hotels, search, amenities, and histograms")
                        .version("v1")
                        .contact(new Contact()
                                .name("Property View Team")
                                .email("support@property-view.local")))
                .servers(List.of(new Server().url("/").description("Default server")));
    }
}

