package com.artinus.api.global.client;

import com.artinus.api.global.client.dto.CsrngProperties;
import com.artinus.api.global.client.dto.CsrngResponseItem;
import com.artinus.api.global.client.exception.CsrngClientException;
import com.artinus.api.global.client.exception.ExternalApiUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CsrngClient {

    private final CsrngProperties properties;

    public int getRandomZeroOrOne() {
        RuntimeException lastException = null;

        for (int attempt = 1; attempt <= properties.maxRetries() + 1; attempt++) {
            try {
                int random = call();

                log.info("csrng 호출에 성공했습니다. attempt={}, random={}", attempt, random);
                return random;

            } catch (ResourceAccessException e) {
                log.warn("csrng 호출 중 타임아웃 또는 네트워크 오류 발생했습니다. attempt={}/{}", attempt, properties.maxRetries() + 1, e);
                lastException = new ExternalApiUnavailableException("외부 구독 검증 API 호출 중 타임아웃 또는 네트워크 오류가 발생했습니다.", e);

            } catch (RestClientException e) {
                log.warn("csrng 호출 중 클라이언트 오류가 발생했습니다. attempt={}/{}", attempt, properties.maxRetries() + 1, e);
                lastException = new ExternalApiUnavailableException("외부 구독 검증 API 호출에 실패했습니다.", e);

            }

            if (attempt <= properties.maxRetries()) {
                sleep(properties.retryDelayMs());
            }
        }
        throw lastException;
    }

    private int call() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.connectTimeoutMs());
        requestFactory.setReadTimeout(properties.readTimeoutMs());

        RestClient restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(requestFactory)
                .build();

        List<CsrngResponseItem> response = restClient.get()
                .uri("/csrng/csrng.php?min=0&max=1")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        if (response == null || response.isEmpty()) {
            throw new CsrngClientException("외부 구독 검증 API 응답이 비어 있습니다.");
        }

        CsrngResponseItem item = response.get(0);

        if (!"success".equalsIgnoreCase(item.status())) {
            throw new CsrngClientException("외부 구독 검증 API 응답 상태가 성공이 아닙니다.");
        }

        if (item.random() != 0 && item.random() != 1) {
            throw new CsrngClientException("외부 구독 검증 API의 random 값이 올바르지 않습니다.");
        }

        return item.random();
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ExternalApiUnavailableException("재시도 대기 중 인터럽트가 발생했습니다.", e);
        }
    }
}