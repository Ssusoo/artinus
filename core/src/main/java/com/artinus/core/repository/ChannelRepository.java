package com.artinus.core.repository;

import com.artinus.core.base.repository.repository.BaseRepository;
import com.artinus.core.domain.channel.Channel;
import com.artinus.core.domain.channel.QChannel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class ChannelRepository extends BaseRepository<Channel, Long> {
    private final QChannel channel = QChannel.channel;

    public Optional<Channel> findByCannelId(Long channelId) {
        return Optional.ofNullable(selectFrom(channel)
                        .where(channel.id.eq(channelId))
                .fetchFirst());
    }
}
