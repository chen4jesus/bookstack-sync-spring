package com.faithconnect.bookstacksync.config;

import com.faithconnect.bookstacksync.model.BookStackConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BookStackConfiguration {

    @Value("${bookstack.source.baseUrl}")
    private String sourceBaseUrl;

    @Value("${bookstack.source.tokenId}")
    private String sourceTokenId;

    @Value("${bookstack.source.tokenSecret}")
    private String sourceTokenSecret;

    @Value("${bookstack.destination.baseUrl}")
    private String destinationBaseUrl;

    @Value("${bookstack.destination.tokenId}")
    private String destinationTokenId;

    @Value("${bookstack.destination.tokenSecret}")
    private String destinationTokenSecret;

    @Bean
    @Primary
    public BookStackConfig sourceConfig() {
        return BookStackConfig.builder()
                .baseUrl(sourceBaseUrl)
                .tokenId(sourceTokenId)
                .tokenSecret(sourceTokenSecret)
                .build();
    }

    @Bean
    public BookStackConfig destinationConfig() {
        return BookStackConfig.builder()
                .baseUrl(destinationBaseUrl)
                .tokenId(destinationTokenId)
                .tokenSecret(destinationTokenSecret)
                .build();
    }
} 