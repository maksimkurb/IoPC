package ru.cubly.iopc.configurator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;

import java.util.Locale;

public class ThymeleafConfig implements WebMvcConfigurer {
    @Bean
    public LocaleResolver localeResolver(@Value("${server.language}") String language) {
        return new FixedLocaleResolver(Locale.forLanguageTag(language));
    }


}
