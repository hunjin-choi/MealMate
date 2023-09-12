package service.chat.mealmate.chat.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import service.chat.mealmate.security.jwt.JwtTokenProvider;

import java.util.Map;


public class WebSockHandShakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // WebSocket 연결 전에 실행되는 로직을 작성합니다.
        // 필요한 인증, 세션 관리 등의 작업을 수행할 수 있습니다.
        // attributes 맵을 사용하여 속성을 설정하고 WebSocketHandler로 전달할 수 있습니다.
        System.out.println("before");
        SecurityContext ctx = SecurityContextHolder.getContext();
        String name = ctx.getAuthentication().getName();
        String[] cookies = request.getHeaders().get("cookie").get(0).split(";");
        for (String keyValue : cookies) {
            if (keyValue.indexOf(JwtTokenProvider.CHAT_TOKEN) != -1) {
                String[] split = keyValue.split("=", 2);
                // chatJWT의 유효성 검사
                jwtTokenProvider.validateToken(split[1]);
                break;
            }
        }
        if (name == "hunjin") {
            return false;
        }
        if (name.equals("hunjin")){
            return false;
        }
        System.out.println("name = " + name);
        // 토큰 확인하고 적절한 사용자이면 통과, 아니면 반려
        return true; // 연결을 계속 진행하려면 true를 반환합니다.
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        System.out.println("after");
        // WebSocket 연결 후에 실행되는 로직을 작성합니다.
        // 예외 처리, 로깅 등의 작업을 수행할 수 있습니다.
    }
}