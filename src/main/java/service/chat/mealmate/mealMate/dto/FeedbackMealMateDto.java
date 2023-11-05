package service.chat.mealmate.mealMate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @AllArgsConstructor
public class FeedbackMealMateDto {
    private Long mealMateId;
    private String receiverName;

    // private Boolean isFeedbacked;
    private LocalTime feedbackTime;
    private String feedbackMention;
    private Integer feedbackMileage;

}
