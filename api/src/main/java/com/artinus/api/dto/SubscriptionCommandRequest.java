package com.artinus.api.dto;

import com.artinus.core.domain.subscription.SubscriptionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SubscriptionCommandRequest(
        @NotBlank(message = "휴대폰번호는 필수입니다.")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "휴대폰번호 형식이 올바르지 않습니다. 예: 010-1234-5678"
        )
        String phoneNumber,

        @NotNull(message = "채널 ID는 필수입니다.")
        Long channelId,

        @NotNull(message = "변경할 구독 상태는 필수입니다.")
        SubscriptionStatus targetStatus
) {
}