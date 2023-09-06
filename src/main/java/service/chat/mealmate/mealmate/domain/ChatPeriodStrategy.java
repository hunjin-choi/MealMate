package service.chat.mealmate.mealmate.domain;

import service.chat.mealmate.mealmate.dto.ChatPeriodDto;

import java.time.LocalTime;

public interface ChatPeriodStrategy {
    public boolean canDeleteImmediately(LocalTime now, ChatPeriod chatPeriod);

    public boolean canAddImmediately(LocalTime now, ChatPeriodDto chatPeriodDto);

    public boolean canUpdateImmediately(LocalTime now, ChatPeriod chatPeriod);

}
