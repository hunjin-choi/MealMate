package service.chat.mealmate.mealMate.domain.vote.validate;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity @NoArgsConstructor
public class TitleVote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long titleVoteId;

    private String title;

    public TitleVote(String title) {
        this.title = title;
    }

    public boolean validate(String title) {
        if (title == null)
            throw new RuntimeException("입력값이 잘 못 되었습니다.");
        return this.title == title;
    }
}
