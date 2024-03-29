package service.chat.mealmate.chat.config;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import service.chat.mealmate.security.ChatPeriodCheck;
import service.chat.mealmate.security.domain.SecurityMember;
import service.chat.mealmate.security.jwt.JwtTokenProvider;

import java.time.LocalDateTime;
import java.util.Map;


public class WebSockHandShakeInterceptor implements HandshakeInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
//    @ChatPeriodCheck // 이 어노테이션으로 인가 절차 진행.
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LocalDateTime chatExpiredAt = principal.getChatExpiredAt();
        // principle에 chatExpiredAt을 설정해주는 작업은 채팅방에 입장하는 API에서 처리합니다.
        if (chatExpiredAt == null || chatExpiredAt.isBefore(LocalDateTime.now()))
            return false;
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String msg = principal.getUsername() + "(id = " + principal.getMemberId() + ")님 웹소켓 연결 성공";
        log.info(msg);
    }
}