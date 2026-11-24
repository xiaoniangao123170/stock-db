package com.ming.stock.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Ming
 * @Description: Springdoc OpenAPI 配置类
 **/
@Configuration
@EnableKnife4j
public class OpenApiConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        Contact contact = new Contact()
                .name("Stock-db Maintainer")
                .url("https://github.com/")
                .email("your_email@example.com");

        return new OpenAPI()
                .info(new Info()
                        .title("A 股行情平台 API 文档")
                        .description("A 股行情数据分析平台的后端接口文档")
                        .contact(contact)
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public-api")
                .packagesToScan("com.ming.stock.controller")
                .pathsToMatch("/**")
                .build();
    }
}