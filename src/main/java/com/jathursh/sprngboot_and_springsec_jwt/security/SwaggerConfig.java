package com.jathursh.sprngboot_and_springsec_jwt.security;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@OpenAPIDefinition  //OpenAPI annotation to enable swagger ** added on 10/03/2023 **
public class SwaggerConfig {

    // -- added on 14/3/2023 --
    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearer-token",
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                ))
                .security(Arrays.asList(new SecurityRequirement()
                        .addList("bearer-token")
                        .addList("refresh-token", "read")
                ))
//                .addSecurityItem(new SecurityRequirement()
//                        .addList("bearer-token")
//                        .addList("refresh-token", "write")
//                        .addList("scope", "read")
//                )
                .info(new Info()
                        .title("Spring Boot and Spring Security with JWT")
                        .version("1.0.0")
                        .description("API Documentation"));
    }
}
