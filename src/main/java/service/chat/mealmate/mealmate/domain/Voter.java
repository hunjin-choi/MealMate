package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Voter {
    @ManyToOne @Id
    private Vote vote;
    @ManyToOne @Id
    private MealMate mealMate;
    private VoterStatus voterStatus;
    private Boolean isCreator;

    public Voter(Vote vote, MealMate mealMate, VoterStatus voterStatus, Boolean isCreator) {
        this.vote = vote;
        this.mealMate = mealMate;
        this.voterStatus = voterStatus;
        this.isCreator = isCreator;
    }
}
