package service.chat.mealmate.mealMate.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import service.chat.mealmate.mealMate.domain.vote.VoteSubject;
import service.chat.mealmate.mealMate.repository.ChatRoomRepository;
import service.chat.mealmate.mealMate.repository.MealMateRepository;
import service.chat.mealmate.mealMate.repository.VoteRepository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
public class ChatRoomJoinPolicy {
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MealMateRepository mealMateRepository;
    private final VoteRepository voteRepository;

    private MealMate alreadyJoined(Member member, ChatRoom chatRoom) {
        return mealMateRepository.findByMemberAndChatRoomAndLeavedAtIsNull(member, chatRoom).orElse(null);
    }
    private boolean isMemberInOtherChatRoom(Member member, ChatRoom chatRoom) {
        List<MealMate> activeMealMateList = mealMateRepository.findAllActiveMealMateBy(member.getMemberId());
        if (activeMealMateList.size() == 0) return false;
        for (MealMate mealMate : activeMealMateList) {
            if (mealMate.getChatRoom() != chatRoom) return true;
        }
        // list 길이가 1이고, 해당 채팅방에 들어가 있는 경우
        return false;
    }
    private boolean isExpired(ChatRoom chatRoom) {
        LocalDateTime closedAt = chatRoom.getClosedAt();
        return closedAt != null && closedAt.isBefore(LocalDateTime.now());
    }

    private boolean isLockVoteActivated(ChatRoom chatRoom) {
        return voteRepository.findActivatedLockChangeVote(chatRoom, VoteSubject.LOCK).size() > 0;
    }

    private boolean isFull(ChatRoom chatRoom) {
        return false;
    }
    // TODO: 2021-10-07 1. 채팅방에 들어갈 수 있는지 확인하는 메소드
    // 고려사항: 다른 채팅방에 이미 들어가있는지, 채팅방이 잠금 투표 진행중인지, 채팅방이 꽉 찼는지, 채팅방이 만료되었는지
    public MealMate canJoinImmediately(Member member, ChatRoom chatRoom) {
        MealMate mealMate = null;
        if ((mealMate = alreadyJoined(member, chatRoom)) != null) {
            return mealMate;
        } else if (isMemberInOtherChatRoom(member, chatRoom)) {
            throw new RuntimeException("이미 다른 채팅방에 참여하고 있습니다.");
        } else if (isLockVoteActivated(chatRoom)) {
            throw new RuntimeException("잠금 투표가 진행중입니다.");
        } else if (isFull(chatRoom)) {
            throw new RuntimeException("채팅방이 꽉 찼습니다.");
        } else if (isExpired(chatRoom)) {
            throw new RuntimeException("채팅방이 만료되었습니다.");
        } else {
            return mealMateRepository.save(new MealMate(member, chatRoom, LocalDateTime.now()));
        }
    }
}
