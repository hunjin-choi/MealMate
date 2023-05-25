package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import service.chat.mealmate.utils.DateUtil;

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
    private int maxPeriod = 500000;

    public ChatPeriod(int startHour, int startMinute, int endHour, int endMinute, MealMate mealMate) {
        ChatTime startChatTime = new ChatTime(startHour, startMinute);
        ChatTime endChatTime = new ChatTime(endHour, endMinute);
        this.startTime = startChatTime;
        this.endTime = endChatTime;
        if (startTime.isLessThanMaxPeriod(endTime, maxPeriod) == false) throw new RuntimeException("");
        this.mealMate = mealMate;
    }

    public Integer chatPeriodCheck(java.util.Date date) {
        if (startTime.isLessThan(date) && endTime.isMoreThan(date)) {
            return endTime.calculateDiffBySecond(date);
        }
        else return null;
    }
}
