package service.chat.mealmate.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.chat.mealmate.member.domain.Role;
import service.chat.mealmate.mileageHistory.domain.Mileage;
import service.chat.mealmate.mileageHistory.domain.MileageChangeReason;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;

import java.util.Date;

@Service @RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MileageHistoryRepository mileageHistoryRepository;
//    public void signUp(String userName) {
//        Member member = new Member(userName, "nickname", "picture", Role.USER);
//        MileageHistory mileageHistory = new MileageHistory(new Mileage(0), new Date(), MileageChangeReason.INIT, member, null);
//        mileageHistoryRepository.save(mileageHistory);
//        memberRepository.save(member);
//    }
}
