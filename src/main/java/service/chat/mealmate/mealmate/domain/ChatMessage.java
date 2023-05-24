package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatMessageId;

    private String message;

    @Temporal(value = TemporalType.DATE)
    private Date date;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "mealmate_id", referencedColumnName = "mealmate_id"),
            @JoinColumn(name = "giver_id", referencedColumnName = "giver_id")
    })
    private MealMate mealMate;

    public ChatMessage(String message, Date date, MealMate mealMate) {
        this.message = message;
        this.date = date;
        this.mealMate = mealMate;
    }
}
