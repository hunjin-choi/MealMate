package service.chat.mealmate.mealMate.domain.vote;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.mealMate.dto.ChatPeriodDto;
import service.chat.mealmate.mealMate.dto.CreateVoteAndVotingDto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class VoteFactory {

    private static List<VoteSubject> voteWithLock = List.of(VoteSubject.ADD_CHAT_PERIOD, VoteSubject.UPDATE_CHAT_PERIOD, VoteSubject.UPDATE_CHAT_ROOM_TITLE);

    private void policyCheck(ChatRoom chatRoom, VoteSubject voteSubject) {
        if (chatRoom.getLockedAt() == null) {
            if (voteWithLock.contains(voteSubject)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잠금 상태가 아닌 채팅방에서는 해당 투표를 생성할 수 없습니다.");
            }
        } else {
            if (!voteWithLock.contains(voteSubject)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 잠금 상태입니다.");
            }
        }
    }
    public Vote createVote(CreateVoteAndVotingDto dto, ChatRoom chatRoom) {
        VoteSubject voteSubject = dto.getVoteSubject();
        policyCheck(chatRoom, voteSubject);
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
