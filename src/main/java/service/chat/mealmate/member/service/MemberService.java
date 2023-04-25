package service.chat.mealmate.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import websocket.spring_websocket.mileage.domain.Mileage;
import websocket.spring_websocket.mileage.domain.MileageChangeReason;
import websocket.spring_websocket.mileage.domain.MileageHistory;
import websocket.spring_websocket.mileage.domain.MileageHistoryRepository;
import websocket.spring_websocket.member.domain.Member;
import websocket.spring_websocket.member.domain.MemberRepository;

import java.util.Date;

@Service @RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MileageHistoryRepository mileageHistoryRepository;
    public void signUp(String userName) {
        Member member = new Member(userName, "nickname");
        MileageHistory mileageHistory = new MileageHistory(new Mileage(10L, 0L), new Date(), MileageChangeReason.INIT, member);
        mileageHistoryRepository.save(mileageHistory);
        memberRepository.save(member);
    }
}
