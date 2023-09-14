package service.chat.mealmate.mealMate.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.time.LocalTime;

@Embeddable @Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatTime {
    private int hour;
    private int minute;

    public ChatTime(int hour, int minute) {
        if (hour < 0 || hour > 23) throw new RuntimeException("");
        if (minute <0 || minute >60) throw new RuntimeException("");
        this.hour = hour;
        this.minute = minute;
    }

    // ChatTime 객체의 시간 차이 계산
    public boolean isLessThanMaxPeriod(ChatTime other, int maxPeriod) {
        int thisTimeInMinutes = this.hour * 60 + this.minute;
        int otherTimeInMinutes = other.hour * 60 + other.minute;

        int diff = Math.abs(thisTimeInMinutes - otherTimeInMinutes);
        return diff <= maxPeriod;
    }

    public boolean isMoreThan(LocalTime other) {
        int hour = other.getHour();
        int minutes = other.getMinute();
        int thisTimeInMinutes = this.hour * 60 + this.minute;
        int otherTimeInMinutes = hour * 60 + minutes;
        return thisTimeInMinutes >= otherTimeInMinutes;
    }
    public boolean isMoreThan(ChatTime other) {
        int hour = other.getHour();
        int minutes = other.getMinute();
        int thisTimeInMinutes = this.hour * 60 + this.minute;
        int otherTimeInMinutes = hour * 60 + minutes;
        return thisTimeInMinutes >= otherTimeInMinutes;
    }
    public boolean isLessThan(LocalTime other) {
        int hour = other.getHour();
        int minutes = other.getMinute();
        int thisTimeInMinutes = this.hour * 60 + this.minute;
        int otherTimeInMinutes = hour * 60 + minutes;
        return thisTimeInMinutes <= otherTimeInMinutes;
    }
    public boolean isLessThan(ChatTime other) {
        int hour = other.getHour();
        int minutes = other.getMinute();
        int thisTimeInMinutes = this.hour * 60 + this.minute;
        int otherTimeInMinutes = hour * 60 + minutes;
        return thisTimeInMinutes <= otherTimeInMinutes;
    }
    public int calculateDiffByMinute(LocalTime other) {
        int hour = other.getHour();
        int minutes = other.getMinute();
        int thisTimeInMinutes = this.hour * 60 + this.minute;
        int otherTimeInMinutes = hour * 60 + minutes;

        return Math.abs(thisTimeInMinutes - otherTimeInMinutes);
    }
    public int calculateDiffByMinute(ChatTime other) {
        int hour = other.getHour();
        int minutes = other.getMinute();
        int thisTimeInMinutes = this.hour * 60 + this.minute;
        int otherTimeInMinutes = hour * 60 + minutes;

        return Math.abs(thisTimeInMinutes - otherTimeInMinutes);
    }
}
