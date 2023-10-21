package service.chat.mealmate.mealMate.domain;

import org.springframework.stereotype.Component;
import service.chat.mealmate.mealMate.dto.ChatPeriodDto;

import java.time.LocalTime;

@Component
public class ChatRoomStatusChangePolicy {
    protected boolean onlyAfterThanEndTime(LocalTime now, ChatPeriod chatPeriod) {
        LocalTime startTime = chatPeriod.startTime;
        LocalTime endTime = chatPeriod.endTime;

        if (now.isAfter(startTime) && now.isBefore(endTime)) return false;
        else if (now.isBefore(startTime)) return false;
        else if (now.isAfter(endTime)) return true;
        else return true; // 여기 도달할 상황이...? 등호 상황?
    }

    public boolean canDeleteImmediately(LocalTime now, ChatPeriod chatPeriod) {
        return onlyAfterThanEndTime(now, chatPeriod);
    }

    public boolean canAddImmediately(LocalTime now, ChatPeriodDto chatPeriodDto) {
        return true;
    }

    public boolean canUpdateImmediately(LocalTime now, ChatPeriod chatPeriod) {
        return onlyAfterThanEndTime(now, chatPeriod);
    }

    public boolean canUpdateTitleImmediately(LocalTime now) {
        return true;
    }
}
