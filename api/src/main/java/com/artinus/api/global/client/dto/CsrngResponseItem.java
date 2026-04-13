package com.artinus.api.global.client.dto;

public record CsrngResponseItem(
        String status,
        int min,
        int max,
        int random
) {
}