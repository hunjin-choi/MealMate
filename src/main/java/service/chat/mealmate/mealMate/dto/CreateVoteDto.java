package service.chat.mealmate.mealMate.dto;

import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.VotingMethodType;

@Data
public class CreateVoteDto {
    private String title;
    private String content;
    private VotingMethodType votingMethodType;
    private VotingDto votingDto;
}
