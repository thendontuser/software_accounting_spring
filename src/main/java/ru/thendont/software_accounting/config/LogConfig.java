package ru.thendont.software_accounting.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.thendont.software_accounting.util.ConstantStrings;

@Configuration
public class LogConfig {

    @Bean
    public Logger logger() {
        return LogManager.getLogger(ConstantStrings.LOGGER_NAME);
    }
}