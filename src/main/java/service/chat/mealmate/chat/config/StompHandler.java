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
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import service.chat.mealmate.chat.dto.RedisChatMessageDto;
import service.chat.mealmate.chat.service.RedisChatPublisherService;
import service.chat.mealmate.security.domain.SecurityMember;
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
    private final RedisChatPublisherService redisChatPublisherService;
    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        accessor.setLeaveMutable(true);
        accessor.isMutable();
        message = MessageBuilder.fromMessage(message).setHeader("connect", "true").build();
        // accessor.setImmutable();
        accessor.setHeader("connect", "true");
        if (accessor.getCommand().equals(StompCommand.DISCONNECT)) {
            // 프론트까지 전달 안됨
            accessor.setHeader("connect", "false");
            message = MessageBuilder.fromMessage(message).setHeader("connect", "false").build();
            SecurityMember securityMember = (SecurityMember) ((UsernamePasswordAuthenticationToken) accessor.getUser()).getPrincipal();
            Long mealMateId = securityMember.getMealMateId();
            Long memberId = securityMember.getMemberId();
            String loginId = securityMember.getUsername();
            String msg = loginId + "님이 퇴장하셨습니다.";
            String chatRoomId = securityMember.getChatRoomId();
            redisChatPublisherService.convertAndSend(msg, loginId, chatRoomId, mealMateId, RedisChatMessageDto.MessageType.QUIT);
//            channel.send(message); -> 재전송
            return message;
        }
//        accessor.setMessage();
        System.out.println("accessor.getCommand() = " + accessor.getCommand());
        return message;
    }
    // 매개변수 sent가 false면 전송 실패 혹은 DISCONNECT 일 것
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (accessor.getCommand().equals(StompCommand.CONNECT)) {
            MessageHeaders messageHeaders = accessor.getMessageHeaders();
        }
        ChannelInterceptor.super.postSend(message, channel, sent);
    }


    public Message<?> preSend2(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Principal user = accessor.getUser(); // springSecurity 연계한 편의기능***
        System.out.println("accessor.getCommand() = " + accessor.getCommand());
        return message;
    }


}
