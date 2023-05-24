package service.chat.mealmate.mealmate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.mealmate.domain.*;
import service.chat.mealmate.mealmate.repository.FeedbackHistoryRepository;
import service.chat.mealmate.mealmate.repository.MealMateRepository;
import service.chat.mealmate.mileageHistory.domain.MileageChangeReason;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.utils.DateUtil;

import java.util.Date;
import java.util.List;

@Service @RequiredArgsConstructor
@Transactional
public class MealmateService {
    private final MealMateRepository mealMateRepository;
    private final MemberRepository memberRepository;
    private final FeedbackHistoryRepository feedbackHistoryRepository;
    private final MileageHistoryRepository mileageHistoryRepository;

    public void connectMealMate(Long member1_id, Long member2_id, String chatRoomId) {
        Date now = DateUtil.getNow();
        MealMate mealMate1 = new MealMate(member1_id, member2_id, now, chatRoomId);
        MealMate mealMate2 = new MealMate(member2_id, member1_id, now, chatRoomId);
        mealMateRepository.save(mealMate1);
        mealMateRepository.save(mealMate2);
    }

    public void disConnectMealMate(Long member1_id, Long member2_id) {
        Date now = DateUtil.getNow();
        MealMate mealMate1 = mealMateRepository.findActiveMealmateByReceiverId(member1_id).orElseThrow(() -> new RuntimeException("적절한 밀메이트가 없습니다."));
        MealMate mealMate2 = mealMateRepository.findActiveMealmateByReceiverId(member2_id).orElseThrow(() -> new RuntimeException("적절한 밀메이트가 없습니다."));
        mealMate1.disconnect(now); mealMate2.disconnect(now);
        // cookie.setMaxAge(0)을 통해 쿠키 삭제 유;
    }

    public void confirm(Long senderId, String confirmMessage, int feedbackMileage) {
        Date now = DateUtil.getNow();
        MealMate mealMate = mealMateRepository.findActiveMealmateByGiverId(senderId).orElseThrow(() -> new RuntimeException("밀 메이트가 없습니다"));
        Member receiver = memberRepository.findById(mealMate.getReceiverId()).orElse(null);

        FeedbackHistory feedbackHistory = mealMate.confirm(confirmMessage, now, feedbackMileage);
        feedbackHistoryRepository.save(feedbackHistory);

        MileageHistory latestMileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(receiver);
        MileageHistory newMileageHistory = latestMileageHistory.createNewHistory(feedbackHistory.getMileagePerFeedback(), MileageChangeReason.FEEDBACK, feedbackHistory.getFeedBackHistoryId(), now);
        mileageHistoryRepository.save(newMileageHistory);
    }

    public void addChatPeriod(Long receiverId, int startHour, int startMinute, int endHour, int endMinute) {
        MealMate mealMate = mealMateRepository.findActiveMealmateByReceiverId(receiverId).orElse(null);
        mealMate.addChatPeriod(startHour, startMinute, endHour, endMinute);
        // chatRepository.save(chatPeriod);
    }

    public void saveChatMessage(String chatMessage, String chatRoomId, Long giverId) {
        MealMate mealMate = mealMateRepository.findByChatRoomIdAndGiverId(chatRoomId, giverId).orElse(null);
        mealMate.addChatMessage(chatMessage);
    }
    public List<ChatMessage> findAllChatMessage(String chatRoomId, Long giverId) {
        return mealMateRepository.findAllChatMessage(chatRoomId, giverId);
    }

    public List<ChatMessage> findChatMessagePageable(String chatRoomId, Long giverId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<ChatMessage> chatMessagePageable = mealMateRepository.findChatMessagePageable(chatRoomId, giverId, pageRequest);
        List<ChatMessage> content = chatMessagePageable.getContent();
        return content;
    }
}
