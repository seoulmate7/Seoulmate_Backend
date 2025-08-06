package com.nexus.seoulmate.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        // SecurityScheme 정의
        SecurityScheme sessionIdScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER)
                .name("sessionId")
                .description("세션 ID를 입력하세요");

        // Components에 SecurityScheme 추가
        Components components = new Components()
                .addSecuritySchemes("sessionId", sessionIdScheme);

        // SecurityRequirement 정의
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("sessionId");

        return new OpenAPI()
                .info(new Info()
                        .title("SeoulMate API")
                        .description("서울메이트 프로젝트의 REST API 명세서입니다.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("SeoulMate Team")
                                .email("wjdekdns0218@gmail.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://www.apache.org/licenses/LICENSE-2.0")))
                .components(components)
                .addSecurityItem(securityRequirement);
    }
}
