package service.chat.mealmate.mealmate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import websocket.spring_websocket.mealmate.domain.FeedbackHistory;
import websocket.spring_websocket.mealmate.domain.FeedbackHistoryRepository;
import websocket.spring_websocket.mealmate.domain.MealMate;
import websocket.spring_websocket.mealmate.domain.MealMateRepository;
import websocket.spring_websocket.mileage.domain.Mileage;
import websocket.spring_websocket.mileage.domain.MileageChangeReason;
import websocket.spring_websocket.mileage.domain.MileageHistory;
import websocket.spring_websocket.mileage.domain.MileageHistoryRepository;
import websocket.spring_websocket.member.domain.Member;
import websocket.spring_websocket.member.domain.MemberRepository;

import java.util.Date;

@Service @RequiredArgsConstructor
public class MealMateService {
    private final MealMateRepository mealMateRepository;
    private final MemberRepository memberRepository;
    private final FeedbackHistoryRepository feedbackHistoryRepository;
    private final MileageHistoryRepository mileageHistoryRepository;

    public void connectMealMate(Long member1_id, Long member2_id) {
        Member member1 = memberRepository.findById(member1_id).orElseThrow(() -> new RuntimeException("member1이 존재하지 않습니다"));
        Member member2 = memberRepository.findById(member2_id).orElseThrow(() -> new RuntimeException("member2이 존재하지 않습니다"));
        MealMate mealMate1 = new MealMate(0L, true, member1, member2);
        MealMate mealMate2 = new MealMate(0L, true, member2, member1);
        mealMateRepository.save(mealMate1); mealMateRepository.save(mealMate2);
    }

    public void disConnectMealMate(Long member1_id, Long member2_id) {
        Member member1 = memberRepository.findById(member1_id).orElseThrow(() -> new RuntimeException("member1이 존재하지 않습니다"));
        Member member2 = memberRepository.findById(member2_id).orElseThrow(() -> new RuntimeException("member2이 존재하지 않습니다"));
        MealMate mealMate1 = mealMateRepository.findByOwnerAndIsActive(member1, true).orElseThrow(() -> new RuntimeException("적절한 밀메이트가 없습니다."));
        MealMate mealMate2 = mealMateRepository.findByOwnerAndIsActive(member2, true).orElseThrow(() -> new RuntimeException("적절한 밀메이트가 없습니다."));
        mealMate1.disconnect(); mealMate2.disconnect();
    }

    public void confirm(Long senderId, String confirmMessage) {
        Member owner = memberRepository.findById(senderId).orElseThrow(() -> new RuntimeException("해당 유저가 없습니다."));
        MealMate mealMate = mealMateRepository.findByOwnerAndIsActive(owner, true).orElseThrow(() -> new RuntimeException("밀 메이트가 없습니다"));
        Member member = mealMate.getMember(); Long givenMileage = 10L;
        mealMate.addMileage(givenMileage);
        FeedbackHistory feedbackHistory = new FeedbackHistory(givenMileage, confirmMessage, new Date(), mealMate);
        feedbackHistoryRepository.save(feedbackHistory);

        MileageHistory oldMileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(member);
        Mileage oldMileage = oldMileageHistory.getMileage();
        Mileage newMileage = oldMileage.appendValueAndCreateNewMileage(givenMileage);
        MileageHistory newMileageHistory = new MileageHistory(newMileage, new Date(), MileageChangeReason.FEEDBACK, member);
        mileageHistoryRepository.save(newMileageHistory);
    }
}
