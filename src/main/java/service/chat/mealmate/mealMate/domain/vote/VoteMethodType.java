package service.chat.mealmate.mealMate.domain.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import service.chat.mealmate.mealMate.dto.EnumMappingStrategy;

public enum VoteMethodType {
    MAJORITY, UNANIMOUS, NONE;

//    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
//    public static VoteMethodType VotingMethodType(@JsonProperty("voteMethodType") String voteMethodType) {
//        return (VoteMethodType) EnumMappingStrategy.validate(values(), voteMethodType);
//    }

    @JsonCreator(mode = JsonCreator.Mode.DEFAULT)
    public static VoteMethodType of(String voteMethodType) {
        return (VoteMethodType) EnumMappingStrategy.validate(values(), voteMethodType);
    }
}
