package com.artinus.core.service;

import com.artinus.core.domain.history.SubscriptionHistory;
import com.artinus.core.repository.SubscriptionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class SubscriptionHistoryQueryService {
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;

    public List<SubscriptionHistory> getHistories(String phoneNumber) {
        return subscriptionHistoryRepository.findBySubscriptionHistories(phoneNumber);
    }
}
