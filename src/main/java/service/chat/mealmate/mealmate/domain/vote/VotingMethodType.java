package service.chat.mealmate.mealmate.domain.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import service.chat.mealmate.mealmate.dto.EnumMappingStrategy;

public enum VotingMethodType {
    MAJORITY, UNANIMOUS, NONE;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static VotingMethodType VotingMethodType(@JsonProperty("votingMethodType") String votingMethodType) {
        return (VotingMethodType) EnumMappingStrategy.validate(values(), votingMethodType);
    }
}
