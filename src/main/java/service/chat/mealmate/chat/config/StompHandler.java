package service.chat.mealmate.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import service.chat.mealmate.security.jwt.JwtTokenProvider;
import service.chat.mealmate.chat.service.ChatService;
import service.chat.mealmate.mealMate.repository.MealMateRepository;
import service.chat.mealmate.member.repository.MemberRepository;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final MemberRepository memberRepository;
    private final MealMateRepository mealMateRepository;
    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand().equals(StompCommand.CONNECT)) {
            MessageHeaders messageHeaders = accessor.getMessageHeaders();
        }
        ChannelInterceptor.super.postSend(message, channel, sent);
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand() == StompCommand.CONNECT) {
            accessor.setNativeHeader("testJWT", "testJWT");
        }
        System.out.println("accessor.getCommand() = " + accessor.getCommand());
        return message;
    }


    public Message<?> preSend2(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Principal user = accessor.getUser(); // springSecurity 연계한 편의기능***
        System.out.println("accessor.getCommand() = " + accessor.getCommand());
        return message;
    }
}
