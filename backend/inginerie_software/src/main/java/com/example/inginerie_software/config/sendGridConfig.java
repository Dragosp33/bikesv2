package com.example.inginerie_software.config;

import com.sendgrid.SendGrid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class sendGridConfig {

    @Bean
    public SendGrid sendGrid() {
        // Instantiate and configure the SendGrid bean
        return new SendGrid("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");
    }
}
