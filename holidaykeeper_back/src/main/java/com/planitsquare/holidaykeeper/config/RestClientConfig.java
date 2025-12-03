package com.planitsquare.holidaykeeper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient dateApiRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl("https://date.nager.at/api/v3")
                .messageConverters(converters -> {
                    for (var converter : converters) {
                        if (converter instanceof MappingJackson2HttpMessageConverter jackson) {
                            var supported = new ArrayList<>(jackson.getSupportedMediaTypes());
                            supported.add(MediaType.valueOf("text/json"));
                            jackson.setSupportedMediaTypes(supported);
                        }
                    }
                })
                .build();
    }
}
