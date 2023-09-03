package service.chat.mealmate.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.member.domain.Role;
import service.chat.mealmate.member.dto.MileageDto;
import service.chat.mealmate.mileageHistory.domain.MileageChangeReason;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service @RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final MileageHistoryRepository mileageHistoryRepository;
    public Member signUp(String userName, Role role) {
        Member member = new Member(userName, userName, "email", "picture", role);
        MileageHistory mileageHistory = new MileageHistory(member, 0, LocalDateTime.now(), MileageChangeReason.INIT);
        memberRepository.save(member);
        mileageHistoryRepository.save(mileageHistory);
        return member;
    }

    public void changeNickname(String memberId, String nickname) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException(""));
        member.changeNickname(nickname);
    }
    public List<MileageDto> dynamicTest(String name) {
        Member member = memberRepository.findById(name).orElse(null);
        return mileageHistoryRepository.dynamicTest(member);
    }

    public List<MileageHistory> findAllMileageHistory(String name) {
        Member member = memberRepository.findById(name).orElse(null);
        return mileageHistoryRepository.findByMemberOrderByCreatedAtAsc(member);
    }
}
