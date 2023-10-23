package service.chat.mealmate.security;


import lombok.AllArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import service.chat.mealmate.security.domain.SecurityMember;

import java.time.LocalDateTime;

@Aspect
@Component
// @Order(value = 1) // Order의 뜻을 제대로 알자..
public class ChatPeriodCheckAdvice {
    @Before("@annotation(ChatPeriodCheck)")
    public void chatPeriodCheck(){
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime chatExpiredAt = principal.getChatExpiredAt();
        // principle에 chatExpiredAt을 설정해주는 작업은 채팅방에 입장하는 API에서 처리합니다.
        if (chatExpiredAt == null || chatExpiredAt.isBefore(LocalDateTime.now()))
            throw new RuntimeException("채팅 가능한 시간대가 아니라서 인가 불가! 이전 메시지를 볼 수는 있지만, 대화를 나눌 수는 없습니다.");
    }
}
