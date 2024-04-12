package de.domesoft.levelupapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.domesoft.levelupapi.dataparse.DataParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("unused")
@Configuration
public class AppConfig {
    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
    @Bean
    public Logger dataParserLogger(){
        return LoggerFactory.getLogger(DataParser.class);
    }
}
