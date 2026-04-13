package com.artinus.api.service;

import com.artinus.api.dto.SubscriptionCommandRequest;
import com.artinus.api.dto.SubscriptionHistoryResponse;
import com.artinus.core.domain.history.SubscriptionHistory;
import com.artinus.core.service.SubscriptionHistoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionHistoryQueryService subscriptionHistoryQueryService;
    private final SubscriptionCreateService subscriptionCreateService;
    private final SubscriptionCancelService subscriptionCancelService;

    public List<SubscriptionHistoryResponse> getSubscriptionHistories(String phoneNumber) {
        List<SubscriptionHistory> histories = subscriptionHistoryQueryService.getHistories(phoneNumber);

        return histories.stream()
                .map(history -> new SubscriptionHistoryResponse(
                        history.getMember().getPhoneNumber(),
                        history.getChannel().getChannelName(),
                        history.getActionType().name(),
                        history.getFromStatus().name(),
                        history.getToStatus().name(),
                        history.getChangedAt()
                ))
                .toList();
    }

    public void createSubscription(SubscriptionCommandRequest request) {
        subscriptionCreateService.subscribe(
                request.phoneNumber(),
                request.channelId(),
                request.targetStatus()
        );
    }

    public void cancelSubscription(SubscriptionCommandRequest request) {
        subscriptionCancelService.cancel(
                request.phoneNumber(),
                request.channelId(),
                request.targetStatus()
        );
    }
}
