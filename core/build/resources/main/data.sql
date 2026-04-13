-- =========================
-- channels
-- =========================
insert into channels (id, name, channel_type) values (1, '홈페이지', 'BOTH');
insert into channels (id, name, channel_type) values (2, '모바일앱', 'BOTH');
insert into channels (id, name, channel_type) values (3, '네이버', 'SUBSCRIBE_ONLY');
insert into channels (id, name, channel_type) values (4, 'SKT', 'SUBSCRIBE_ONLY');
insert into channels (id, name, channel_type) values (5, '콜센터', 'CANCEL_ONLY');
insert into channels (id, name, channel_type) values (6, '이메일', 'CANCEL_ONLY');

-- =========================
-- members
-- =========================
insert into members (id, phone_number) values (1, '010-1111-2222');
insert into members (id, phone_number) values (2, '010-2222-3333');
insert into members (id, phone_number) values (3, '010-3333-4444');
insert into members (id, phone_number) values (4, '010-4444-5555');

-- =========================
-- subscriptions (현재 상태 테이블)
-- =========================
insert into subscriptions (id, member_id, current_status, version) values (1, 1, 'BASIC', 0);
insert into subscriptions (id, member_id, current_status, version) values (2, 2, 'PREMIUM', 0);
insert into subscriptions (id, member_id, current_status, version) values (3, 3, 'NONE', 0);
insert into subscriptions (id, member_id, current_status, version) values (4, 4, 'BASIC', 0);

-- =========================
-- subscription_history
-- =========================

-- member 1: NONE -> BASIC (홈페이지 가입)
insert into subscription_history
(id, member_id, channel_id, action_type, from_status, to_status, changed_at)
values
    (1, 1, 1, 'SUBSCRIBE', 'NONE', 'BASIC', '2026-01-01 09:00:00');

-- member 2: NONE -> BASIC (네이버 가입)
insert into subscription_history
(id, member_id, channel_id, action_type, from_status, to_status, changed_at)
values
    (2, 2, 3, 'SUBSCRIBE', 'NONE', 'BASIC', '2026-01-03 10:00:00');

-- member 2: BASIC -> PREMIUM (모바일앱 업그레이드)
insert into subscription_history
(id, member_id, channel_id, action_type, from_status, to_status, changed_at)
values
    (3, 2, 2, 'SUBSCRIBE', 'BASIC', 'PREMIUM', '2026-02-01 14:30:00');

-- member 3: NONE -> BASIC (홈페이지 가입)
insert into subscription_history
(id, member_id, channel_id, action_type, from_status, to_status, changed_at)
values
    (4, 3, 1, 'SUBSCRIBE', 'NONE', 'BASIC', '2026-01-05 11:20:00');

-- member 3: BASIC -> NONE (이메일 해지)
insert into subscription_history
(id, member_id, channel_id, action_type, from_status, to_status, changed_at)
values
    (5, 3, 6, 'CANCEL', 'BASIC', 'NONE', '2026-02-10 16:00:00');

-- member 4: NONE -> BASIC (SKT 가입)
insert into subscription_history
(id, member_id, channel_id, action_type, from_status, to_status, changed_at)
values
    (6, 4, 4, 'SUBSCRIBE', 'NONE', 'BASIC', '2026-03-01 13:10:00');