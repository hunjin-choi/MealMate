package service.chat.mealmate.chat.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import service.chat.mealmate.chat.dto.ChatMessage;
import service.chat.mealmate.chat.jwt.JwtTokenProvider;
import service.chat.mealmate.chat.service.ChatService;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("accessor.getCommand() = " + accessor.getCommand());
//        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand()))
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            jwtTokenProvider.validateReadOnlyToken(accessor.getFirstNativeHeader("readOnlyToken"));
        }
        // StompCommand.SEND <-> preSend에서의 Send  둘은 다른것!!
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            // jwt 토큰이 있는지 검증
            jwtTokenProvider.validateReadWriteToken(accessor.getFirstNativeHeader("readWriteToken"));
            // jwt 토큰이 현재 채팅방에서 유효한 토큰인지 검증 (=chatRoomId 값을 통해서)
            String roomIdFromJWT = jwtTokenProvider.getChatRoomIdFromJWT(accessor.getFirstNativeHeader("readWriteToken"));
            String roomIdFromEndpoint = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            if (!roomIdFromJWT.equals(roomIdFromEndpoint)) {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
            }
        }
        // websocket SEND 할 때도 헤더의 jwt token 검증?
        return message;
    }
}
