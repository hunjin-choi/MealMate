package service.chat.mealmate.mealMate.domain.vote.validate;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatPeriodVote {
    @Id @GeneratedValue
    private Long chatPeriodVoteId;

    private LocalTime startTime;
    private LocalTime endTime;

    public ChatPeriodVote(LocalTime startTime, LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public boolean validate(Integer startHour, Integer startMinutes, Integer endHour, Integer endMinute) {
        if (startHour == null || startMinutes == null || endHour == null || endMinute == null)
            throw new RuntimeException("입력 값이 잘 못 되었습니다.");
        return startTime.getHour() == startHour && startTime.getMinute() == startMinutes
                && endTime.getHour() == endHour && endTime.getMinute() == endMinute;
    }
}
