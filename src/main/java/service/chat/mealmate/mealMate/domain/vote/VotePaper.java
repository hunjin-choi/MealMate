package service.chat.mealmate.mealMate.domain.vote;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealMate.domain.MealMate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames={"vote_id", "meal_mate_id"}))
public class VotePaper implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long votePaperId;
    @ManyToOne(cascade = CascadeType.ALL) @JoinColumn(name = "vote_id")
    private Vote vote;
    @ManyToOne @JoinColumn(name = "meal_mate_id")
    private MealMate mealMate;
    @Getter
    @Enumerated(EnumType.STRING)
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
