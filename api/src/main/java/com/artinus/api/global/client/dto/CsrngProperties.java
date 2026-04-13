package com.artinus.api.global.client.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "subscription.csrng")
public record CsrngProperties(
        String baseUrl,
        int connectTimeoutMs,
        int readTimeoutMs,
        int maxRetries,
        long retryDelayMs
) {
}
