package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.sql.Date;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatPeriod {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatPeriodId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "hour", column = @Column(name = "start_hour")),
            @AttributeOverride(name = "minutes", column = @Column(name = "start_minutes"))
    })
    ChatTime startTime;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "hour", column = @Column(name = "end_hour")),
            @AttributeOverride(name = "minutes", column = @Column(name = "end_minutes"))
    })
    ChatTime endTime;

    // java.sql.Date 타입으로 @Temporal 어노테이션 필요 없음
    Date latestFeedbackDate;
    @ManyToOne
    MealMate mealMate;

    @Value("")
    private int maxPeriod = 50;

    public ChatPeriod(int startHour, int startMinute, int endHour, int endMinute, MealMate mealMate) {
        ChatTime startChatTime = new ChatTime(startHour, startMinute);
        ChatTime endChatTime = new ChatTime(endHour, endMinute);
        if (startTime.isLessThanMaxPeriod(endTime, maxPeriod) == false) throw new RuntimeException("");

        this.startTime = startChatTime;
        this.endTime = endChatTime;
        this.mealMate = mealMate;
    }
}
