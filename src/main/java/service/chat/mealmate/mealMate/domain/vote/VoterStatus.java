package service.chat.mealmate.mealMate.domain.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import service.chat.mealmate.mealMate.dto.EnumMappingStrategy;

public enum VoterStatus {
    AGREE, DISAGREE;
//    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
//    public static VoterStatus VoterStatus (@JsonProperty(value = "voterStatus") String voterStatus) {
//        return (VoterStatus) EnumMappingStrategy.validate(values(), voterStatus);
//    }
    @JsonCreator
    public static VoterStatus of(String voterStatus) {
        return (VoterStatus) EnumMappingStrategy.validate(values(), voterStatus);
    }
}
