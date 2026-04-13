package com.artinus.core.repository;

import com.artinus.core.base.repository.repository.BaseRepository;
import com.artinus.core.domain.history.QSubscriptionHistory;
import com.artinus.core.domain.history.SubscriptionHistory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubscriptionHistoryRepository extends BaseRepository<SubscriptionHistory, Long> {
    private final QSubscriptionHistory subscriptionHistory = QSubscriptionHistory.subscriptionHistory;

    public List<SubscriptionHistory> findBySubscriptionHistories(String phoneNumber) {
        return selectFrom(subscriptionHistory)
                .where(subscriptionHistory.member.phoneNumber.eq(phoneNumber))
                .orderBy(subscriptionHistory.changedAt.asc())
                .fetch();
    }
}
