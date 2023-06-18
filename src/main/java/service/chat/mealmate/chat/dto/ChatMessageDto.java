package service.chat.mealmate.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import service.chat.mealmate.mealmate.domain.ChatMessage;
import service.chat.mealmate.mealmate.dto.ChatPeriodDto;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class ChatMessageDto {
    // 메시지 타입 : 입장, 채팅
    public enum MessageType {
        ENTER, JOIN, TALK
    }
    private MessageType type; // 메시지 타입
    private String roomId; // 방번호
    private String sender; // 메시지 보낸사람
    private String message; // 메시지
    private Date date;

    public ChatMessageDto(String message, Date date) {
        this.message = message;
        this.date = date;
    }

    public ChatMessageDto(String message, Date date, String sender, String roomId) {
        this.message = message;
        this.date = date;
        this.sender = sender;
        this.roomId = roomId;
    }
    static public ChatMessageDto entityToDto(ChatMessage chatMessage) {
        return new ChatMessageDto(chatMessage.getMessage(), chatMessage.getDate());
    }

    static public List<ChatMessageDto> entityToDtoList(List<ChatMessage> chatMessageList) {
        return chatMessageList.stream()
                .map(ChatMessageDto::entityToDto)
                .collect(Collectors.toList());
    }
}