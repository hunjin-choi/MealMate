package service.chat.mealmate.mealMate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.chat.dto.ChatMessageType;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    private String message;

    private LocalDateTime sentAt;

    private ChatMessageType chatMessageType;

    @ManyToOne()
    private MealMate mealMate;

    public ChatMessage(String message, LocalDateTime date, MealMate mealMate) {
        this.message = message;
        this.sentAt = date;
        this.mealMate = mealMate;
    }
}
