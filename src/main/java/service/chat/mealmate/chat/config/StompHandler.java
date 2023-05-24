package service.chat.mealmate.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorSubscribableChannel;
import org.springframework.stereotype.Component;
import service.chat.mealmate.chat.jwt.JwtTokenProvider;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            // 유효한 readOnly-jwt가 있는지 검증
            // 클라이언트의 구독 요청을 가로채고 정책을 확인하여 처리
            String destination = accessor.getDestination();
            Long chatRoomId;
            if (!isSubscriptionAllowed(destination)) {
                // 구독 거부 처리
                throw new RuntimeException("");
//                throw new SubscriptionDeniedException("Subscription is not allowed for this destination.");
            }
        }
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("token"));
        }
        // StompCommand.SEND <-> preSend에서의 Send
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("token"));
        }
        // websocket SEND 할 때도 헤더의 jwt token 검증?
        return message;
    }

    private boolean isSubscriptionAllowed(String destination) {
        // 정책 확인 로직을 구현
        // 필요한 비즈니스 규칙, 권한 등을 확인하여 구독을 허용 또는 거부할 수 있음
        return true; // 허용된 경우 true, 거부된 경우 false 반환
    }
}
