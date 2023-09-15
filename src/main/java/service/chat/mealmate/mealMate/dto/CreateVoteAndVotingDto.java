package service.chat.mealmate.mealMate.dto;

import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.VoteMethodType;
import service.chat.mealmate.mealMate.domain.vote.VoteSubject;

import java.time.LocalDateTime;

@Data
public class CreateVoteAndVotingDto {
    private String voteTitle;
    private String content;
    private VoteMethodType voteMethodType;
    private VoteSubject voteSubject;

    private VotingDto votingDto;
}
