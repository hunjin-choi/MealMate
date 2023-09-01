package service.chat.mealmate.mealmate.domain;

import org.springframework.stereotype.Component;
import service.chat.mealmate.mealmate.dto.ChatPeriodDto;

import java.time.LocalTime;

@Component
public class ChatPeriodStrategy {
    protected boolean onlyMoreThanEndTime(LocalTime now, ChatPeriod chatPeriod) {
        LocalTime startTime = chatPeriod.startTime;
        LocalTime endTime = chatPeriod.endTime;

        if (now.isAfter(startTime) && now.isBefore(endTime)) return false;
        else if (now.isBefore(startTime)) return false;
        else if (now.isAfter(endTime)) return true;
        else return true; // 여기 도달할 상황이...?
    }

    public boolean canDeleteImmediately(LocalTime now, ChatPeriod chatPeriod) {
        return onlyMoreThanEndTime(now, chatPeriod);
    }

    public boolean canAddImmediately(LocalTime now, ChatPeriodDto chatPeriodDto) {
        return true;
    }

    public boolean canUpdateImmediately(LocalTime now, ChatPeriod chatPeriod) {
        return onlyMoreThanEndTime(now, chatPeriod);
    }
}