package service.chat.mealmate.mealmate.domain.vote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealmate.domain.MealMate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VotePaper implements Serializable {
    @ManyToOne @Id
    private Vote vote;
    @ManyToOne @Id
    private MealMate mealMate;
    @Getter
    private VoterStatus voterStatus;
    private Boolean isCreator;

    private LocalDateTime votedAt;
    public VotePaper(Vote vote, MealMate mealMate, VoterStatus voterStatus, Boolean isCreator) {
        this.vote = vote;
        this.mealMate = mealMate;
        this.voterStatus = voterStatus;
        this.isCreator = isCreator;
    }


}
