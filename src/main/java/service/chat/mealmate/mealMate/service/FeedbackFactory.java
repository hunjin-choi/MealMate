package service.chat.mealmate.mealMate.service;


import service.chat.mealmate.mealMate.domain.ChatPeriod;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.mealMate.domain.FeedbackHistory;
import service.chat.mealmate.mealMate.domain.MealMate;

import java.time.LocalDateTime;

public class FeedbackFactory {
    public static FeedbackHistory createFeedbackHistory(String feedbackMention, MealMate giver, MealMate receiver, ChatPeriod chatPeriod, LocalDateTime feedBackDateTime, Integer feedbackMileage, ChatRoom chatRoom) {
        if (chatRoom.getLockedAt() == null) {
            throw new RuntimeException("잠금 상태가 아닌 채팅방에서는 피드백을 남길 수 없습니다.");
        }
        return new FeedbackHistory(feedbackMention, giver, receiver, chatPeriod, feedBackDateTime, feedbackMileage);
    }
}
