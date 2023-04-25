package service.chat.mealmate.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.chat.mealmate.mileage.domain.Mileage;
import service.chat.mealmate.mileage.domain.MileageChangeReason;
import service.chat.mealmate.mileage.domain.MileageHistory;
import service.chat.mealmate.mileage.domain.MileageHistoryRepository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.domain.MemberRepository;

import java.util.Date;

@Service @RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MileageHistoryRepository mileageHistoryRepository;
    public void signUp(String userName) {
        Member member = new Member(userName, "nickname");
        MileageHistory mileageHistory = new MileageHistory(new Mileage(0L), new Date(), MileageChangeReason.INIT, member, null);
        mileageHistoryRepository.save(mileageHistory);
        memberRepository.save(member);
    }
}
