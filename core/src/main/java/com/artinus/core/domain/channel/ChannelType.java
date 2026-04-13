package com.artinus.core.domain.channel;

public enum ChannelType {
    BOTH,           // 구독/해지 모두 가능
    SUBSCRIBE_ONLY, // 구독만 가능
    CANCEL_ONLY;     // 해지만 가능

    // 구독 가능
    public boolean canSubscribe() {
        return this == BOTH || this == SUBSCRIBE_ONLY;
    }

    // 해지 가능
    public boolean canCancel() {
        return this == BOTH || this == CANCEL_ONLY;
    }
}
