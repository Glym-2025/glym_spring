package gllim.gllim_spring.global.config;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


@OpenAPIDefinition(
        info = @Info(
                title = "Glym-2025 프로젝트",
                description = """
                        AI 기반 필기체 제작 시스템의 api 문서입니다.
                        """,
                version = "1.0v"
        ),
        servers = {
                @Server(url = "https://main.server.com", description = "메인 서버"),
                @Server(url = "https://test.server.com", description = "테스트 서버"),
                @Server(url = "http://localhost:8080", description = "로컬 서버")
        }
)


@Configuration
public class SwaggerConfig {
        @Bean
        public OpenAPI openAPI() {
                SecurityScheme apiKey = new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .in(SecurityScheme.In.HEADER)
                        .name("Authorization")
                        .scheme("bearer")
                        .bearerFormat("JWT");

                SecurityRequirement securityRequirement = new SecurityRequirement()
                        .addList("Bearer Token");

                return new OpenAPI()
                        .components(new Components().addSecuritySchemes("Bearer Token", apiKey))
                        .addSecurityItem(securityRequirement)
                        .servers(List.of(
                                        new io.swagger.v3.oas.models.servers.Server()
                                                .url("http://localhost:8080")
                                                .description("로컬 서버"),
                                        new io.swagger.v3.oas.models.servers.Server()
                                                .url("https://test.server.com")
                                                .description("테스트 서버"),
                                        new io.swagger.v3.oas.models.servers.Server()
                                                .url("https://main.server.com")
                                                .description("메인 서버")
                                )
                        );
        }
}
