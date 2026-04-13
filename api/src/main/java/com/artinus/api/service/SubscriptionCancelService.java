package com.artinus.api.service;

import com.artinus.api.global.client.exception.ClientException;
import com.artinus.core.domain.channel.Channel;
import com.artinus.core.domain.history.SubscriptionActionType;
import com.artinus.core.domain.history.SubscriptionHistory;
import com.artinus.core.domain.member.Member;
import com.artinus.core.domain.subscription.Subscription;
import com.artinus.core.domain.subscription.SubscriptionStatus;
import com.artinus.core.exception.CannelException;
import com.artinus.core.exception.MemberException;
import com.artinus.core.exception.SubscriptionException;
import com.artinus.core.repository.ChannelRepository;
import com.artinus.core.repository.MemberRepository;
import com.artinus.core.repository.SubscriptionHistoryRepository;
import com.artinus.core.repository.SubscriptionRepository;
import com.artinus.api.global.client.CsrngClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubscriptionCancelService {
    private final MemberRepository memberRepository;
    private final ChannelRepository channelRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionHistoryRepository subscriptionHistoryRepository;
    private final CsrngClient csrngClient;

    @Transactional
    public void cancel(String phoneNumber, Long channelId, SubscriptionStatus targetStatus) {
        Member member = memberRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));

        Channel channel = channelRepository.findByCannelId(channelId)
                .orElseThrow(() -> new CannelException("존재하지 않는 채널입니다."));

        if (!channel.canCancel()) {
            throw new CannelException("해지가 불가능한 채널입니다.");
        }

        Subscription subscription = subscriptionRepository.findByMember(member)
                .orElseThrow(() -> new SubscriptionException("구독 정보가 존재하지 않습니다."));

        SubscriptionStatus currentStatus = subscription.getCurrentStatus();

        validateCancelTransition(currentStatus, targetStatus);

        int random = csrngClient.getRandomZeroOrOne();
        if (random == 0) {
            throw new ClientException("외부 API 결과로 인해 해지 처리에 실패했습니다.");
        }

        subscription.changeStatus(targetStatus);

        subscriptionHistoryRepository.save(
                SubscriptionHistory.create(
                        member,
                        channel,
                        SubscriptionActionType.CANCEL,
                        currentStatus,
                        targetStatus
                )
        );
    }

    private void validateCancelTransition(SubscriptionStatus current, SubscriptionStatus target) {
        if (current == SubscriptionStatus.PREMIUM &&
                (target == SubscriptionStatus.BASIC || target == SubscriptionStatus.NONE)) {
            return;
        }

        if (current == SubscriptionStatus.BASIC && target == SubscriptionStatus.NONE) {
            return;
        }

        throw new SubscriptionException("해지 가능한 상태 변경이 아닙니다.");
    }
}
