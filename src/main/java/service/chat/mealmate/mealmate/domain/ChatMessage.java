package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    private String message;

    private LocalDateTime sentAt;

    @ManyToOne()
    private MealMate mealMate;

    public ChatMessage(String message, LocalDateTime date, MealMate mealMate) {
        this.message = message;
        this.sentAt = date;
        this.mealMate = mealMate;
    }
}
