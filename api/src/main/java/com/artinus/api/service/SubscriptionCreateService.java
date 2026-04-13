package com.artinus.api.service;

import com.artinus.api.global.client.CsrngClient;
import com.artinus.api.global.client.exception.ClientException;
import com.artinus.core.domain.channel.Channel;
import com.artinus.core.domain.history.SubscriptionActionType;
import com.artinus.core.domain.history.SubscriptionHistory;
import com.artinus.core.domain.member.Member;
import com.artinus.core.domain.subscription.Subscription;
import com.artinus.core.domain.subscription.SubscriptionStatus;
import com.artinus.core.exception.CannelException;
import com.artinus.core.exception.SubscriptionException;
import com.artinus.core.repository.ChannelRepository;
import com.artinus.core.repository.MemberRepository;
import com.artinus.core.repository.SubscriptionHistoryRepository;
import com.artinus.core.repository.SubscriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionCreateService {
    private final MemberRepository memberRepository;
    private final ChannelRepository channelRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final CsrngClient csrngClient;

    @Transactional
    public void subscribe(String phoneNumber, Long channelId, SubscriptionStatus targetStatus) {
        Member member = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseGet(() -> memberRepository.save(Member.create(phoneNumber)));

        Channel channel = channelRepository.findByCannelId(channelId)
                .orElseThrow(() -> new CannelException("존재하지 않는 채널입니다."));

        if (!channel.canSubscribe()) {
            throw new CannelException("구독이 불가능한 채널입니다.");
        }

        Subscription subscription = subscriptionRepository.findByMember(member)
                .orElseGet(() -> subscriptionRepository.save(
                        Subscription.create(member, SubscriptionStatus.NONE)
                ));

        SubscriptionStatus currentStatus = subscription.getCurrentStatus();

        validateSubscribeTransition(currentStatus, targetStatus);

        int random = csrngClient.getRandomZeroOrOne();

        if (random == 0) {
            throw new ClientException("외부 API 결과로 인해 구독 처리에 실패했습니다.");
        }

        subscription.changeStatus(targetStatus);

        subscriptionHistoryRepository.save(
                SubscriptionHistory.create(
                        member,
                        channel,
                        SubscriptionActionType.SUBSCRIBE,
                        currentStatus,
                        targetStatus
                )
        );
    }

    private void validateSubscribeTransition(SubscriptionStatus current, SubscriptionStatus target) {
        if (current == SubscriptionStatus.NONE &&
                (target == SubscriptionStatus.BASIC || target == SubscriptionStatus.PREMIUM)) {
            return;
        }

        if (current == SubscriptionStatus.BASIC && target == SubscriptionStatus.PREMIUM) {
            return;
        }
        throw new SubscriptionException("구독 가능한 상태 변경이 아닙니다.");
    }
}
