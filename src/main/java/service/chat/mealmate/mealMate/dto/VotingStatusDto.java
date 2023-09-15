package service.chat.mealmate.mealMate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class VotingStatusDto {
    private Long personnel;
    private Long agree;
    private Long disagree;
}
