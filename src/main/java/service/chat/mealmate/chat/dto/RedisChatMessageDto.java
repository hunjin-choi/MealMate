package service.chat.mealmate.chat.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.chat.mealmate.mealMate.domain.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RedisChatMessageDto {

    public RedisChatMessageDto(ChatMessageType type, String roomId, String sender, String message) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
    }

    private ChatMessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime date;

    public RedisChatMessageDto(String message, LocalDateTime date) {
        this.message = message;
        this.date = date;
    }

    public RedisChatMessageDto(String message, LocalDateTime date, String sender, String roomId) {
        this.message = message;
        this.date = date;
        this.sender = sender;
        this.roomId = roomId;
    }
    static public RedisChatMessageDto entityToDto(ChatMessage chatMessage) {
        return new RedisChatMessageDto(chatMessage.getMessage(), chatMessage.getSentAt());
    }

    static public List<RedisChatMessageDto> entityToDtoList(List<ChatMessage> chatMessageList) {
        return chatMessageList.stream()
                .map(RedisChatMessageDto::entityToDto)
                .collect(Collectors.toList());
    }
}