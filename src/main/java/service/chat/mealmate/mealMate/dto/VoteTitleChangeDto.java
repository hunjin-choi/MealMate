package service.chat.mealmate.mealMate.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import service.chat.mealmate.mealMate.domain.vote.VoteMethodType;
import service.chat.mealmate.mealMate.domain.vote.VoteSubject;

import java.time.LocalDateTime;

@Data @AllArgsConstructor
public class VoteTitleChangeDto {
    private Long voteId;
    private String voteTitle;
    private String content;
    private VoteMethodType voteMethodType;
    private VoteSubject voteSubject;
    private LocalDateTime createdAt;
    private LocalDateTime completedDate;

    private String chatRoomTitle;

    public VoteTitleChangeDto(Long voteId, String voteTitle, String content, VoteMethodType voteMethodType, VoteSubject voteSubject, LocalDateTime createdAt, String chatRoomTitle) {
        this.voteId = voteId;
        this.voteTitle = voteTitle;
        this.content = content;
        this.voteMethodType = voteMethodType;
        this.voteSubject = voteSubject;
        this.createdAt = createdAt;
        this.chatRoomTitle = chatRoomTitle;
    }
}