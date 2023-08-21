package service.chat.mealmate.mealmate.domain;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;
    private String title;
    private String content;
    private VoteType voteType;

    public Vote(String title, String content, VoteType voteType) {
        this.title = title;
        this.content = content;
        this.voteType = voteType;
    }
}
