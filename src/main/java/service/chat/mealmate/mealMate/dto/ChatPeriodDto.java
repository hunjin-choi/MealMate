package service.chat.mealmate.mealMate.dto;

import lombok.Getter;
import service.chat.mealmate.mealMate.domain.ChatPeriod;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChatPeriodDto {
    private Long chatPeriodId;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;


    public ChatPeriodDto(Long chatPeriodId, int startHour, int startMinute, int endHour, int endMinute) {
        this.chatPeriodId = chatPeriodId;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    static public ChatPeriodDto entityToDto(ChatPeriod chatPeriod) {
        return new ChatPeriodDto(chatPeriod.getChatPeriodId(), chatPeriod.getStartTime().getHour(), chatPeriod.getEndTime().getHour()
        , chatPeriod.getStartTime().getMinute(), chatPeriod.getEndTime().getMinute());
    }

    static public List<ChatPeriodDto> entityToDtoList(List<ChatPeriod> chatPeriodList) {
        return chatPeriodList.stream()
                .map(ChatPeriodDto::entityToDto)
                .collect(Collectors.toList());
    }
}
