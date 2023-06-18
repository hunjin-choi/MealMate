package service.chat.mealmate.mealmate.dto;

import lombok.Getter;
import service.chat.mealmate.mealmate.domain.ChatPeriod;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChatPeriodDto {
    private Long chatPeriodId;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private Date latestFeedbackDate;


    public ChatPeriodDto(Long chatPeriodId, int startHour, int startMinute, int endHour, int endMinute, Date latestFeedbackDate) {
        this.chatPeriodId = chatPeriodId;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
        this.latestFeedbackDate = latestFeedbackDate;
    }

    static public ChatPeriodDto entityToDto(ChatPeriod chatPeriod) {
        return new ChatPeriodDto(chatPeriod.getChatPeriodId(), chatPeriod.getStartTime().getHour(), chatPeriod.getEndTime().getHour()
        , chatPeriod.getStartTime().getMinutes(), chatPeriod.getEndTime().getMinutes(), chatPeriod.getLatestFeedbackDate());
    }

    static public List<ChatPeriodDto> entityToDtoList(List<ChatPeriod> chatPeriodList) {
        return chatPeriodList.stream()
                .map(ChatPeriodDto::entityToDto)
                .collect(Collectors.toList());
    }
}
