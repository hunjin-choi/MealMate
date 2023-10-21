package service.chat.mealmate.mealMate.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import service.chat.mealmate.mealMate.repository.ChatRoomRepository;
import service.chat.mealmate.mealMate.repository.MealMateRepository;
import service.chat.mealmate.mealMate.repository.VoteRepository;
import service.chat.mealmate.member.repository.MemberRepository;

@Component
@AllArgsConstructor
public class ChatRoomEnterPolicy {
    private final MealMateRepository mealMateRepository;
    public MealMate canEnterImmediately(Long memberId, String chatRoomId ) {
        return mealMateRepository.findOneActivatedCompositeBy(memberId, chatRoomId)
                .orElseThrow(() -> new RuntimeException("채팅방 멤버가 아닙니다."));
    }
}
