package br.com.asap.api.config;

import com.github.javafaker.Faker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class FakerConfig {

    @Bean
    public Faker createFaker() {
        return Faker.instance(Locale.forLanguageTag("pt-BR"));
    }

}
