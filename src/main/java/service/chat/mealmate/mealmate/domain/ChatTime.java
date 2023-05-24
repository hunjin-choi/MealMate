package service.chat.mealmate.mealmate.domain;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatTime {
    private int hour;
    private int minutes;

    public ChatTime(int hour, int minutes) {
        if (hour < 0 || hour > 23) throw new RuntimeException("");
        if (minutes <0 || minutes >60) throw new RuntimeException("");
        this.hour = hour;
        this.minutes = minutes;
    }

    // ChatTime 객체의 시간 차이 계산
    public boolean isLessThanMaxPeriod(ChatTime other, int maxPeriod) {
        int thisTimeInMinutes = this.hour * 60 + this.minutes;
        int otherTimeInMinutes = other.hour * 60 + other.minutes;

        int diff = Math.abs(thisTimeInMinutes - otherTimeInMinutes);
        return diff <= maxPeriod;
    }

}
