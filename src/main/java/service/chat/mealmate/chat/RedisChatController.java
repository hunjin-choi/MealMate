package service.chat.mealmate.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import service.chat.mealmate.chat.dto.RedisChatMessageDto;
import service.chat.mealmate.chat.service.RedisChatPublisherService;
import service.chat.mealmate.security.ChatPeriodCheck;
import service.chat.mealmate.security.domain.SecurityMember;
import service.chat.mealmate.security.jwt.JwtTokenProvider;
import service.chat.mealmate.mealMate.service.MealMateService;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Native;
import java.security.Principal;

@RequiredArgsConstructor
@Controller
@ControllerAdvice
public class RedisChatController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final MealMateService mealmateService;
    private final RedisChatPublisherService redisChatPublisherService;
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message/{roomId}") // 아래에서 인가 로직 부분은 나중에 AOP 방식이나 혹은 Security 쪽에서 처리하게 바꿀 예정
    public void message(RedisChatMessageDto message, @Header("connect") String connect, Principal principal) {
        // 아래에 httpServletRequest 객체의 참조값이 null이 나오는 이유 설명
        HttpServletRequest requestAttributes = (HttpServletRequest) RequestContextHolder.getRequestAttributes();
        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) principal;
        SecurityMember securityMember = (SecurityMember) authentication.getPrincipal();
        String loginId = securityMember.getUsername();
        Long mealMateId = securityMember.getMealMateId();
        redisChatPublisherService.convertAndSend(message, loginId, mealMateId);
    }
}