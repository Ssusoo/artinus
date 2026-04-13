package com.artinus.core.domain.history;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubscriptionHistory is a Querydsl query type for SubscriptionHistory
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubscriptionHistory extends EntityPathBase<SubscriptionHistory> {

    private static final long serialVersionUID = 1320635853L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubscriptionHistory subscriptionHistory = new QSubscriptionHistory("subscriptionHistory");

    public final EnumPath<SubscriptionActionType> actionType = createEnum("actionType", SubscriptionActionType.class);

    public final DateTimePath<java.time.LocalDateTime> changedAt = createDateTime("changedAt", java.time.LocalDateTime.class);

    public final com.artinus.core.domain.channel.QChannel channel;

    public final EnumPath<com.artinus.core.domain.subscription.SubscriptionStatus> fromStatus = createEnum("fromStatus", com.artinus.core.domain.subscription.SubscriptionStatus.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.artinus.core.domain.member.QMember member;

    public final EnumPath<com.artinus.core.domain.subscription.SubscriptionStatus> toStatus = createEnum("toStatus", com.artinus.core.domain.subscription.SubscriptionStatus.class);

    public QSubscriptionHistory(String variable) {
        this(SubscriptionHistory.class, forVariable(variable), INITS);
    }

    public QSubscriptionHistory(Path<? extends SubscriptionHistory> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubscriptionHistory(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubscriptionHistory(PathMetadata metadata, PathInits inits) {
        this(SubscriptionHistory.class, metadata, inits);
    }

    public QSubscriptionHistory(Class<? extends SubscriptionHistory> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.channel = inits.isInitialized("channel") ? new com.artinus.core.domain.channel.QChannel(forProperty("channel")) : null;
        this.member = inits.isInitialized("member") ? new com.artinus.core.domain.member.QMember(forProperty("member")) : null;
    }

}

