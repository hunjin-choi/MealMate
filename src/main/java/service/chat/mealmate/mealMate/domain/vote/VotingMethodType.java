package service.chat.mealmate.mealMate.domain.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import service.chat.mealmate.mealMate.dto.EnumMappingStrategy;

public enum VotingMethodType {
    MAJORITY, UNANIMOUS, NONE;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static VotingMethodType VotingMethodType(@JsonProperty("votingMethodType") String votingMethodType) {
        return (VotingMethodType) EnumMappingStrategy.validate(values(), votingMethodType);
    }
}
