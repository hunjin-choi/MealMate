package service.chat.mealmate.mealmate.domain;

import lombok.*;
import service.chat.mealmate.mileageHistory.domain.Mileage;
import service.chat.mealmate.utils.DateUtil;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FeedbackHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedBackHistoryId;

    private Integer feedbackMileage;

    private String feedbackMention;

    @Temporal(value = TemporalType.DATE)
    private LocalDate feedBackDate;

    @Temporal(value = TemporalType.TIME)
    private LocalTime feedBackTime;

    @ManyToOne
    private MealMate giver;

    @ManyToOne
    private MealMate receiver;

    @OneToOne(targetEntity = ChatPeriod.class)
    private ChatPeriod chatPeriod;
    @Transient
    private int maxFeedbackMileage = 100;
    @Transient
    private int minFeedbackMileage = 0;
    protected FeedbackHistory(String feedbackMention, MealMate giver, MealMate receiver, ChatPeriod chatPeriod, LocalDate feedBackDate, LocalTime feedbackTime, Integer feedbackMileage) {
        this.feedbackMention = feedbackMention;
        this.giver = giver;
        this.receiver = receiver;
        this.chatPeriod = chatPeriod;
        this.feedBackDate = feedBackDate;
        this.feedBackTime = feedbackTime;
        if (feedbackMileage > this.maxFeedbackMileage || feedbackMileage < this.minFeedbackMileage) throw new RuntimeException("");
        this.feedbackMileage = feedbackMileage;
    }

    static public FeedbackHistory of(String feedbackMention, MealMate giver, MealMate receiver, ChatPeriod chatPeriod, LocalDateTime feedBackDateTime, Integer feedbackMileage) {
        return new FeedbackHistory(feedbackMention, giver, receiver, chatPeriod, feedBackDateTime.toLocalDate(), feedBackDateTime.toLocalTime(),feedbackMileage);
    }
}
