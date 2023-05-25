package service.chat.mealmate.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import service.chat.mealmate.chat.dto.ChatMessage;
import service.chat.mealmate.chat.jwt.JwtTokenProvider;
import service.chat.mealmate.mealmate.service.MealmateService;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Controller
public class ChatController {
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChannelTopic channelTopic;
    private final MealmateService mealmateService;
    /**
     * websocket "/pub/chat/message"로 들어오는 메시징을 처리한다.
     */
    @MessageMapping("/chat/message") // 아래에서 인가 로직 부분은 나중에 AOP 방식이나 혹은 Security 쪽에서 처리하게 바꿀 예정
    public void message(ChatMessage message, @Header("token") String token) {
        // 아래에 httpServletRequest 객체의 참조값이 null이 나오는 이유 설명
        HttpServletRequest requestAttributes = (HttpServletRequest) RequestContextHolder.getRequestAttributes();
        String nickname = jwtTokenProvider.getUserNameFromJwt(token);
        String chatRoomId = jwtTokenProvider.getChatRoomIdFromJWT(token);
        // 로그인 회원 정보로 대화명 설정
        if (nickname == null) nickname = "TempNickname";
        message.setSender(nickname);
        // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            message.setSender("[알림]");
            message.setMessage(nickname + "님이 입장하셨습니다.");
        } else {
            mealmateService.saveChatMessage(message.getMessage(), chatRoomId, nickname);
        }
//        mealmateService.saveChatMessage(message.getMessage(), );
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}