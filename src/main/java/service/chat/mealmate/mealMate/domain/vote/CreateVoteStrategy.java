package service.chat.mealmate.mealMate.domain.vote;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.mealMate.dto.ChatPeriodDto;
import service.chat.mealmate.mealMate.dto.CreateVoteAndVotingDto;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class CreateVoteStrategy {

    public Vote createVote(CreateVoteAndVotingDto dto, ChatRoom chatRoom) {
        VoteSubject voteSubject = dto.getVoteSubject();
        if (voteSubject == VoteSubject.ADD_CHAT_PERIOD || voteSubject == VoteSubject.UPDATE_CHAT_PERIOD) {
            ChatPeriodDto chatPeriodDto = dto.getChatPeriod();
            LocalTime startTime = LocalTime.of(chatPeriodDto.getStartHour(), chatPeriodDto.getStartMinute());
            LocalTime endTime = LocalTime.of(chatPeriodDto.getEndHour(), chatPeriodDto.getEndMinute());
            return Vote.of(dto.getVoteTitle(), dto.getContent(), dto.getVoteMethodType(), dto.getVoteSubject(), chatRoom, LocalDateTime.now(), startTime, endTime);
        } else if (voteSubject == VoteSubject.UPDATE_CHAT_ROOM_TITLE) {
            String chatRoomTitle = dto.getChatRoomTitle();
            return Vote.of(dto.getVoteTitle(), dto.getContent(), dto.getVoteMethodType(), dto.getVoteSubject(), chatRoom, LocalDateTime.now(), chatRoomTitle);
        } else {
            return Vote.of(dto.getVoteTitle(), dto.getContent(), dto.getVoteMethodType(), dto.getVoteSubject(), chatRoom, LocalDateTime.now());
        }
    }
}
