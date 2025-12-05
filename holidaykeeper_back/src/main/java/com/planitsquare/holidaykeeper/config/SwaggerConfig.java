package com.planitsquare.holidaykeeper.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI holidayKeeperAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("HolidayKeeper API")
                        .description("PlanitSquare 채용 과제 HolidayKeeper API 문서")
                        .version("v1.0.0"));
    }
}