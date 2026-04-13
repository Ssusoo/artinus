package com.artinus.core.domain.channel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String channelName;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel_type", nullable = false, length = 30)
    private ChannelType channelType;

    public boolean canSubscribe() {
        return channelType.canSubscribe();
    }

    public boolean canCancel() {
        return channelType.canCancel();
    }
}
