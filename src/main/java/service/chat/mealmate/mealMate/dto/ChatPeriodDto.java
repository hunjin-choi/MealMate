package service.chat.mealmate.mealMate.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import service.chat.mealmate.mealMate.domain.ChatPeriod;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChatPeriodDto {
    private Long chatPeriodId;
    private Integer startHour;
    private Integer startMinute;
    private Integer endHour;
    private Integer endMinute;

    @JsonCreator
    public static ChatPeriodDto of (Long chatPeriodId, Integer startHour, Integer startMinute, Integer endHour, Integer endMinute) {
        return new ChatPeriodDto(chatPeriodId, startHour, startMinute, endHour, endMinute);
    }
    public ChatPeriodDto(Long chatPeriodId, Integer startHour, Integer startMinute, Integer endHour, Integer endMinute) {
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
