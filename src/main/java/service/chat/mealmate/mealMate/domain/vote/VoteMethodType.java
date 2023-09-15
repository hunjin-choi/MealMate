package service.chat.mealmate.mealMate.domain.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import service.chat.mealmate.mealMate.dto.EnumMappingStrategy;

public enum VoteMethodType {
    MAJORITY, UNANIMOUS, NONE;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static VoteMethodType VotingMethodType(@JsonProperty("votingMethodType") String votingMethodType) {
        return (VoteMethodType) EnumMappingStrategy.validate(values(), votingMethodType);
    }
}
