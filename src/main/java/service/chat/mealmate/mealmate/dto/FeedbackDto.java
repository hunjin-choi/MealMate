package service.chat.mealmate.mealmate.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Value;

@Data
public class FeedbackDto {
    public String feedbackMention;
    public int mileage;
    public String receiverName;
}
