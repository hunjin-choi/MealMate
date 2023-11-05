package service.chat.mealmate.mealMate.dto;

import lombok.Data;

@Data
public class FeedbackDto {
    public String feedbackMention;
    public int mileage;
    public Long receiverMealMateId;
    public String receiverNickname;
}
