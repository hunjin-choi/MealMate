package service.chat.mealmate.mealMate.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class FeedbackHistoryDto {
    private String giverNickname;

    private Integer feedbackMileage;

    private String feedbackMention;

    private LocalDate feedbackDate;

    private LocalTime feedbackTime;

    public FeedbackHistoryDto(String giverNickname, Integer feedbackMileage, String feedbackMention, LocalDate feedbackDate, LocalTime feedbackTime) {
        this.giverNickname = giverNickname;
        this.feedbackMileage = feedbackMileage;
        this.feedbackMention = feedbackMention;
        this.feedbackDate = feedbackDate;
        this.feedbackTime = feedbackTime;
    }
}
