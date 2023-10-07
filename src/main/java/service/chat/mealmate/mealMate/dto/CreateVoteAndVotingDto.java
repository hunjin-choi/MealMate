package service.chat.mealmate.mealMate.dto;

import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.VoteMethodType;
import service.chat.mealmate.mealMate.domain.vote.VoteSubject;

import java.time.LocalDateTime;

@Data
public class CreateVoteAndVotingDto {
    private VoteSubject voteSubject;
    private String voteTitle;
    private String content;
    private VoteMethodType voteMethodType;

    private ChatPeriodDto chatPeriodDto = null;
    private String chatRoomTitle = null;
    private Boolean locking = null;

    private VotingDto votingDto;
}
