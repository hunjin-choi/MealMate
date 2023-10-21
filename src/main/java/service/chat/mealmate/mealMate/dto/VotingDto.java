package service.chat.mealmate.mealMate.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.VoterStatus;

@Data @AllArgsConstructor
public class VotingDto {
    private Long voteId;
    private VoterStatus voterStatus;

    @JsonCreator
    public static VotingDto of(String voteId, VoterStatus voterStatus) {
        return new VotingDto(Long.parseLong(voteId), voterStatus);
    }

}
