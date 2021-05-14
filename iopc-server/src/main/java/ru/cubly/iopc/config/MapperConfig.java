package ru.cubly.iopc.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.integration.support.json.JsonObjectMapper;
import org.springframework.integration.support.json.JsonObjectMapperProvider;

@Configuration
public class MapperConfig {
    @Bean
    JsonObjectMapper<?, ?> jsonObjectMapper(ObjectMapper objectMapper) {
        return new Jackson2JsonObjectMapper(objectMapper);
    }

}
