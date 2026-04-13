package com.artinus.core.repository;

import com.artinus.core.base.repository.repository.BaseRepository;
import com.artinus.core.domain.member.Member;
import com.artinus.core.domain.member.QMember;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MemberRepository extends BaseRepository<Member, Long> {
    private final QMember member = QMember.member;

    public Optional<Member> findByPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(selectFrom(member)
                .where(member.phoneNumber.eq(phoneNumber))
                .fetchFirst());
    }
}
