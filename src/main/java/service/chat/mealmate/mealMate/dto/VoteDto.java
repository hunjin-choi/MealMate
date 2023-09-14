package service.chat.mealmate.mealMate.dto;

import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.VoteSubject;

@Data
public class VoteDto {
    private Long voteID;
    private VoteSubject voteSubject;
}
