package service.chat.mealmate.mealmate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mileage.domain.MileageHistory;

import javax.persistence.*;
import java.util.Date;

@Entity @Builder @AllArgsConstructor @NoArgsConstructor @Getter
public class FeedbackHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedBackHistoryId;

    private Long temporalMileage;

    private String feedbackMention;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date feedBackDate;

    @ManyToOne
    private MealMate mealMate;

    public FeedbackHistory(Long temporalMileage, String feedbackMention, Date feedBackDate, MealMate mealMate) {
        this.temporalMileage = temporalMileage;
        this.feedbackMention = feedbackMention;
        this.feedBackDate = feedBackDate;
        this.mealMate = mealMate;
    }
}
