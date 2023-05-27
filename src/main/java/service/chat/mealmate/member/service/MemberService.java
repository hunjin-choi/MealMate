package service.chat.mealmate.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.member.domain.Role;
import service.chat.mealmate.member.dto.MileageDto;
import service.chat.mealmate.mileageHistory.domain.Mileage;
import service.chat.mealmate.mileageHistory.domain.MileageChangeReason;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;

import java.util.Date;
import java.util.List;

@Service @RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final MileageHistoryRepository mileageHistoryRepository;
    public void signUp(String userName, Role role) {
        Member member = new Member(userName, userName, "email", "picture", role);
        MileageHistory mileageHistory = new MileageHistory(new Mileage(0), new Date(), MileageChangeReason.INIT, member, null);
        memberRepository.save(member);
        mileageHistoryRepository.save(mileageHistory);
    }

    public List<MileageDto> dynamicTest(String name) {
        Member member = memberRepository.findById(name).orElse(null);
        return mileageHistoryRepository.dynamicTest(member);
    }

    public List<MileageHistory> findAllMileageHistory(String name) {
        Member member = memberRepository.findById(name).orElse(null);
        return mileageHistoryRepository.findByMemberOrderByDateAsc(member);
    }
}
