package com.artinus.api.client;

public record CsrngResponseItem(
        String status,
        int min,
        int max,
        int random
) {
}