package service.chat.mealmate.mealmate.domain.vote;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import service.chat.mealmate.mealmate.dto.EnumMappingStrategy;

public enum VoterStatus {
    AGREE, DISAGREE;
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public static VoterStatus VoterStatus (@JsonProperty(value = "voterStatus") String voterStatus) {
        return (VoterStatus) EnumMappingStrategy.validate(values(), voterStatus);
    }
}
