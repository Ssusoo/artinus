package com.artinus.core.domain.subscription;

import com.artinus.core.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subscriptions")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(name = "current_status", nullable = false, length = 20)
    private SubscriptionStatus currentStatus;

    @Version
    private Long version;

    public Subscription(Member member, SubscriptionStatus currentStatus) {
        this.member = member;
        this.currentStatus = currentStatus;
    }

    public void changeStatus(SubscriptionStatus targetStatus) {
        this.currentStatus = targetStatus;
    }

    public static Subscription create(Member member, SubscriptionStatus currentStatus) {
        return Subscription.builder()
                .member(member)
                .currentStatus(currentStatus)
                .build();
    }
}
