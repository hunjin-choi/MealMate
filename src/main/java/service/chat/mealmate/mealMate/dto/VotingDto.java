package service.chat.mealmate.mealMate.dto;

import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.VoterStatus;

@Data
public class VotingDto {
    private Long voteId;
    private VoterStatus voterStatus;
}
