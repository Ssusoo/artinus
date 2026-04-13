package com.artinus.core.domain.history;

import com.artinus.core.domain.channel.Channel;
import com.artinus.core.domain.member.Member;
import com.artinus.core.domain.subscription.SubscriptionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubscriptionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 20)
    private SubscriptionActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "before_status", nullable = false, length = 20)
    private SubscriptionStatus beforeStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "after_status", nullable = false, length = 20)
    private SubscriptionStatus afterStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public SubscriptionHistory(
            Member member,
            Channel channel,
            SubscriptionActionType actionType,
            SubscriptionStatus beforeStatus,
            SubscriptionStatus afterStatus,
            LocalDateTime changedAt
    ) {
        this.member = member;
        this.channel = channel;
        this.actionType = actionType;
        this.beforeStatus = beforeStatus;
        this.afterStatus = afterStatus;
        this.changedAt = changedAt;
    }
}
