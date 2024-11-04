package com.toletter.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .apiInfo(apiInfo())
                .securityContexts(securityContext()) // SecurityContext 설정
                .securitySchemes(apiKey()) // ApiKey 설정
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.toletter"))
                .paths(PathSelectors.any())
                .build()
                .useDefaultResponseMessages(false);
    }

    private ApiInfo apiInfo() {  // API의 이름, 현재 버전, API에 대한 정보
        return new ApiInfoBuilder()
                .title("to-Letter API")
                .version("1.0.0")
                .description("to-Letter API 명세서")
                .build();
    }

    // JWT SecurityContext 구성
    private List<SecurityContext> securityContext() {
        List<SecurityContext> contextList = new ArrayList<>();
        contextList.add(SecurityContext.builder().securityReferences(defaultAuth("Access")).build());
        contextList.add(SecurityContext.builder().securityReferences(defaultAuth("Refresh")).build());
        return contextList;
    }

    private List<SecurityReference> defaultAuth(String apiKey) {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference(apiKey, authorizationScopes));
    }

    // ApiKey 정의
    private List<SecurityScheme> apiKey() {
        List<SecurityScheme> apiKeyList = new ArrayList<>();
        apiKeyList.add(new ApiKey("Access", "Authorization", "header"));
        apiKeyList.add(new ApiKey("Refresh", "refreshToken", "header"));
        return apiKeyList;
    }
}
