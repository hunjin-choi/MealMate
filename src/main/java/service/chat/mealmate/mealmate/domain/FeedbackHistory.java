package service.chat.mealmate.mealmate.domain;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FeedbackHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedBackHistoryId;

    private Integer mileagePerFeedback;

    private String feedbackMention;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date feedBackDate;

    @ManyToOne
    private MealMate mealMate;

    @Value("")
    private int maxFeedbackMileage = 100;
    @Value("")
    private int minFeedbackMileage = 0;
    public FeedbackHistory(String feedbackMention, Date feedBackDate, int feedbackMileage, MealMate mealMate) {
        this.feedbackMention = feedbackMention;
        this.feedBackDate = feedBackDate;
        this.mealMate = mealMate;
        if (feedbackMileage > this.maxFeedbackMileage || feedbackMileage < this.minFeedbackMileage) throw new RuntimeException("");
        this.mileagePerFeedback = feedbackMileage;
    }
}
