package service.chat.mealmate.chat.dto;

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
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, JOIN, TALK
    }
    private MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
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