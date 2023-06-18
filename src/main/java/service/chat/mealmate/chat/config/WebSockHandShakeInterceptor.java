package service.chat.mealmate.chat.config;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSockHandShakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // WebSocket 연결 전에 실행되는 로직을 작성합니다.
        // 필요한 인증, 세션 관리 등의 작업을 수행할 수 있습니다.
        // attributes 맵을 사용하여 속성을 설정하고 WebSocketHandler로 전달할 수 있습니다.
        System.out.println("before");
        return true; // 연결을 계속 진행하려면 true를 반환합니다.
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        System.out.println("after");
        // WebSocket 연결 후에 실행되는 로직을 작성합니다.
        // 예외 처리, 로깅 등의 작업을 수행할 수 있습니다.
    }
}