package service.chat.mealmate.mealmate.dto;

import lombok.Data;
import service.chat.mealmate.mealmate.domain.VoteSubject;

@Data
public class VoteDto {
    private Long voteID;
    private VoteSubject voteSubject;
}
