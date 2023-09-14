package service.chat.mealmate.mealMate.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"feedback_date", "chat_period_id", "giver_id", "receiver_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FeedbackHistory implements MileageHistoryReferable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackHistoryId;

    @Getter
    private Integer feedbackMileage;

    private String feedbackMention;

    @Column(name = "feedback_date")
    private LocalDate feedbackDate;

    private LocalTime feedBackTime;

    @ManyToOne
    @JoinColumn(name = "giver_id")
    private MealMate giver;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private MealMate receiver;

    @OneToOne(targetEntity = ChatPeriod.class)
    @JoinColumn(name = "chat_period_id")
    private ChatPeriod chatPeriod;
    @Transient
    private int maxFeedbackMileage = 100;
    @Transient
    private int minFeedbackMileage = 0;

    public FeedbackHistory(String feedbackMention, MealMate giver, MealMate receiver, ChatPeriod chatPeriod, LocalDateTime feedBackDateTime, Integer feedbackMileage) {
        this.feedbackMention = feedbackMention;
        this.giver = giver;
        this.receiver = receiver;
        this.chatPeriod = chatPeriod;
        this.feedbackDate = feedBackDateTime.toLocalDate();
        this.feedBackTime = feedBackDateTime.toLocalTime();
        if (feedbackMileage > this.maxFeedbackMileage || feedbackMileage < this.minFeedbackMileage) throw new RuntimeException("적절한 마일리지 값이 아닙니다.");
        this.feedbackMileage = feedbackMileage;
    }
}
