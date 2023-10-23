package service.chat.mealmate.mealMate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.Vote;
import service.chat.mealmate.mealMate.domain.vote.VotePaper;
import service.chat.mealmate.mealMate.domain.vote.VoterStatus;

import java.util.List;
import java.util.stream.Collectors;

@Data @AllArgsConstructor
public class VotingStatusDto {
    private Long personnel;
    private Long agree;
    private Long disagree;
}
