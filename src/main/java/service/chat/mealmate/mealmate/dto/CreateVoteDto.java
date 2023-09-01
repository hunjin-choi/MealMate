package service.chat.mealmate.mealmate.dto;

import lombok.Data;
import service.chat.mealmate.mealmate.domain.vote.VotingMethodType;

@Data
public class CreateVoteDto {
    private Long creatorId;
    private String title;
    private String content;
    private VotingMethodType votingMethodType;
    private VotingDto votingDto;
}
