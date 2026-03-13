package top.aliangmu.bookkeepingapp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Swagger {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("bookkeeping-App API 管理")
                        .version("1.0")
                        .description("接口文档"));
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi
                .builder()
                .group("用户端")
                .pathsToMatch("/user/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi
                .builder()
                .group("管理端")
                .pathsToMatch("/admin/**")
                .build();
    }
}
