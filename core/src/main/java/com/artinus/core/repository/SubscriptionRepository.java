package com.artinus.core.repository;

import com.artinus.core.base.repository.repository.BaseRepository;
import com.artinus.core.domain.member.Member;
import com.artinus.core.domain.subscription.QSubscription;
import com.artinus.core.domain.subscription.Subscription;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class SubscriptionRepository extends BaseRepository<Subscription, Long> {
    private final QSubscription subscription = QSubscription.subscription;

    public Optional<Subscription> findByMember(Member member) {
        return Optional.ofNullable(selectFrom(subscription).where(subscription.member.eq(member))
                .fetchOne());
    }
}
