package service.chat.mealmate.mealmate.dto;

import lombok.Data;
import service.chat.mealmate.mealmate.domain.vote.VoterStatus;

@Data
public class VotingDto {
    public VoterStatus voterStatus;
}
