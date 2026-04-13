package com.artinus.api.dto;

import java.time.LocalDateTime;

public record SubscriptionHistoryResponse(
        String phoneNumber,
        String channelName,
        String actionType,
        String fromStatus,
        String toStatus,
        LocalDateTime changedAt
) {
}
