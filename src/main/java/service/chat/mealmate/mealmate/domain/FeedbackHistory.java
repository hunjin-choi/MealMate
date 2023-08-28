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
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"feedback_date", "chat_period_id", "giver_id", "receiver_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackHistory implements MileageObject{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackHistoryId;

    @Getter
    private Integer feedbackMileage;

    private String feedbackMention;

    @Temporal(value = TemporalType.DATE)
    private LocalDate feedbackDate;

    @Temporal(value = TemporalType.TIME)
    private LocalTime feedBackTime;

    @ManyToOne
    @Column(name = "giver_id")
    private MealMate giver;

    @ManyToOne
    @Column(name = "receiver_id")
    private MealMate receiver;

    @OneToOne(targetEntity = ChatPeriod.class)
    @Column(name = "chat_period_id")
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
        this.feedbackDate = feedBackDate;
        this.feedBackTime = feedbackTime;
        if (feedbackMileage > this.maxFeedbackMileage || feedbackMileage < this.minFeedbackMileage) throw new RuntimeException("");
        this.feedbackMileage = feedbackMileage;
    }

    static public FeedbackHistory of(String feedbackMention, MealMate giver, MealMate receiver, ChatPeriod chatPeriod, LocalDateTime feedBackDateTime, Integer feedbackMileage) {
        return new FeedbackHistory(feedbackMention, giver, receiver, chatPeriod, feedBackDateTime.toLocalDate(), feedBackDateTime.toLocalTime(),feedbackMileage);
    }
}
