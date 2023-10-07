package service.chat.mealmate.mealMate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.VoteMethodType;
import service.chat.mealmate.mealMate.domain.vote.VoteSubject;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data @AllArgsConstructor
public class VoteChatPeriodChangeDto {
    private Long voteId;
    private String voteTitle;
    private String content;
    private VoteMethodType voteMethodType;
    private VoteSubject voteSubject;
    private LocalDateTime createdAt;
    private LocalDateTime completedDate;

    private LocalTime startTime;
    private LocalTime endTime;

    public VoteChatPeriodChangeDto(Long voteId, String voteTitle, String content, VoteMethodType voteMethodType, VoteSubject voteSubject, LocalDateTime createdAt, LocalTime startTime, LocalTime endTime) {
        this.voteId = voteId;
        this.voteTitle = voteTitle;
        this.content = content;
        this.voteMethodType = voteMethodType;
        this.voteSubject = voteSubject;
        this.createdAt = createdAt;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
