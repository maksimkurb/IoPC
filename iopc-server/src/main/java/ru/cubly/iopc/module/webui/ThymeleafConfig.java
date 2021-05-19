package ru.cubly.iopc.module.webui;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.FixedLocaleResolver;
import nz.net.ultraq.thymeleaf.LayoutDialect;

import java.util.Locale;

public class ThymeleafConfig implements WebMvcConfigurer {
    @Bean
    public LocaleResolver localeResolver(@Value("${server.language}") String language) {
        return new FixedLocaleResolver(Locale.forLanguageTag(language));
    }

    @Bean
    public LayoutDialect layoutDialect() {
        return new LayoutDialect();
    }

}
