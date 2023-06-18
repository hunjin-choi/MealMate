package service.chat.mealmate.mealmate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.mealmate.domain.*;
import service.chat.mealmate.mealmate.dto.ChatPeriodDto;
import service.chat.mealmate.mealmate.dto.FeedbackDto;
import service.chat.mealmate.mealmate.repository.ChatPeriodRepository;
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
    private final ChatPeriodRepository chatPeriodRepository;

//    public void connectMealMate(String member1_id, String member2_id, String chatRoomId) {
//        Date now = DateUtil.getNow();
//        MealMate mealMate1 = new MealMate(member1_id, member2_id, now, chatRoomId);
//        MealMate mealMate2 = new MealMate(member2_id, member1_id, now, chatRoomId);
//        mealMateRepository.save(mealMate1);
//        mealMateRepository.save(mealMate2);
//    }
//
//    public void disConnectMealMate(String member1_id, String member2_id) {
//        Date now = DateUtil.getNow();
//        MealMate mealMate1 = mealMateRepository.findActiveMealmateByReceiverId(member1_id).orElseThrow(() -> new RuntimeException("적절한 밀메이트가 없습니다."));
//        MealMate mealMate2 = mealMateRepository.findActiveMealmateByReceiverId(member2_id).orElseThrow(() -> new RuntimeException("적절한 밀메이트가 없습니다."));
//        mealMate1.disconnect(now); mealMate2.disconnect(now);
//        // cookie.setMaxAge(0)을 통해 쿠키 삭제 유;
//    }

    public void confirm(String senderId, FeedbackDto feedbackDto, String roomId, Long chatPeriodId) {
        String confirmMessage = feedbackDto.getFeedbackMention();
        int feedbackMileage = feedbackDto.getMileage();
        Date now = DateUtil.getNow();
        MealMate mealMate = mealMateRepository.findActiveMealMateByGiverIdAndChatRoomId(senderId, roomId).orElseThrow(() -> new RuntimeException("밀 메이트가 없습니다"));
        Member receiver = memberRepository.findById(mealMate.getReceiverId()).orElse(null);

        FeedbackHistory feedbackHistory = mealMate.confirm(confirmMessage, now, feedbackMileage);
        feedbackHistoryRepository.save(feedbackHistory);

        MileageHistory latestMileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(receiver);
        MileageHistory newMileageHistory = latestMileageHistory.createNewHistory(feedbackHistory.getMileagePerFeedback(), MileageChangeReason.FEEDBACK, feedbackHistory.getFeedBackHistoryId(), now);
        mileageHistoryRepository.save(newMileageHistory);
    }

    public void addChatPeriod(String giverId, ChatPeriodDto chatPeriodDto) {
        MealMate mealMate = mealMateRepository.findActiveMealmateByGiverId(giverId).orElse(null);
        mealMate.addChatPeriod(chatPeriodDto.getStartHour(), chatPeriodDto.getStartMinute(), chatPeriodDto.getEndHour(), chatPeriodDto.getEndMinute());
        // chatRepository.save(chatPeriod);
    }

    public void deleteChatPeriod(String giverId, Long chatPeriodId) {
        MealMate mealMate = mealMateRepository.findActiveMealmateByGiverId(giverId).orElse(null);
        mealMate.deleteChatPeriod(chatPeriodId);
    }

    public void saveChatMessage(String chatMessage, String chatRoomId, String giverId) {
        MealMate mealMate = mealMateRepository.findByChatRoomIdAndGiverId(chatRoomId, giverId).orElse(null);
        mealMate.addChatMessage(chatMessage);
    }
    public List<ChatMessage> findAllChatMessage(String chatRoomId, String giverId) {
        return mealMateRepository.findAllChatMessage(chatRoomId, giverId);
    }

    public List<ChatMessage> findChatMessagePageable(String chatRoomId, String giverId, int pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(0, 1);
        Page<ChatMessage> chatMessagePageable = mealMateRepository.findChatMessagePageable(chatRoomId, giverId, pageRequest);
        List<ChatMessage> content = chatMessagePageable.getContent();
        return content;
    }

    public List<MealMate> findALlMealmate(String name) {
        return mealMateRepository.findByReceiverId(name);
    }
    public List<FeedbackHistory> findAllFeedbackHistory(Long mealmateId) {
        MealMate mealMate = mealMateRepository.findById(mealmateId).orElseThrow(() -> new RuntimeException(""));
        return mealMate.getFeedbackHistoryList();
    }

    public List<ChatPeriod> findAllChatPeriod(String name) {
        MealMate mealMate = mealMateRepository.findActiveMealmateByGiverId(name).orElse(null);
        return mealMate.getChatPeriodList();
    }
}
