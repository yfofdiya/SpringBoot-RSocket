package com.simform.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.rsocket.metadata.SimpleAuthenticationEncoder;

@Configuration
public class RSocketConfig {

    @Value("${rsocket.port}")
    private int port;

    @Bean
    public RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder
                .rsocketStrategies(s -> s.encoder(new SimpleAuthenticationEncoder()))
                .tcp("localhost", port);
    }
}
