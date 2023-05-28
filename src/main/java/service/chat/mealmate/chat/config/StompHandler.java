package service.chat.mealmate.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import service.chat.mealmate.chat.jwt.JwtTokenProvider;
import service.chat.mealmate.chat.service.ChatService;
import service.chat.mealmate.mealmate.domain.ChatPeriod;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.utils.DateUtil;

import java.security.Principal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final ChatService chatService;
    private final MemberRepository memberRepository;
    // websocket을 통해 들어온 요청이 처리 되기전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        Principal user = accessor.getUser(); // springSecurity 연계한 편의기능
        System.out.println("accessor.getCommand() = " + accessor.getCommand());
//        OAuth2User principal = ((OAuth2AuthenticationToken) user).getPrincipal();
        // websocket 연결시 헤더의 jwt token 검증
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String name = user.getName();
            // name 값을 통해 데이터베이스 뒤져서 적절한 chatPeriod 찾아서 jwt token 만들기
            Member member = memberRepository.findById(name).orElse(null);
            String chatRoomId = accessor.getFirstNativeHeader("chatRoomId");
            Long chatPeriodId = null;
            Date expiredDate = null;
            String accessToken = jwtTokenProvider.generateChatAccessToken(name, chatRoomId, chatPeriodId, expiredDate);
            String refreshToken = jwtTokenProvider.generateChatRefreshToken(name, chatRoomId, chatPeriodId, DateUtil.addDaysFromNow(30));
            // accessToken, refreshToken 만들어서 클라이언트에게 전달
            accessor.setHeader("accessToken", accessToken);
            accessor.setHeader("refreshToken", refreshToken);
            // refreshToken은 서버에 저장 (member table에 저장)
            member.connectChatRoom("refreshToken");
            // 삭제예정
            jwtTokenProvider.validateReadOnlyToken(accessor.getFirstNativeHeader("readOnlyToken"));
        }
        // StompCommand.SEND <-> preSend에서의 Send  둘은 다름
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            // accessToken 검증
            String accessToken = accessor.getFirstNativeHeader("accessToken");
            try{
                // validate jwt
            }catch (Exception e) {
                // 데이터베이스의 refreshToken 과 비교
                String refreshToken = accessor.getFirstNativeHeader("refreshToken");
                // 일치하면 유효성 검사
                // 불일치하면 데이터베이스의 refreshToken 유효성 검사 및 채팅 가능 시간대인지 확인하기
                if (false) {
                    throw new RuntimeException("채팅 가능 시간대가 아닙니다");
                }
                // 만약 채팅 가능 시간이면
                // accessor.setHeader("refreshToken", refreshToken); 해서 클라이언트가 refreshToken 값 업데이트 할 수 있게
                // accessor.setHeader("accessToken", accessToken); 해서 클라이언트가 accessToken 값 업데이트 할 수 있게
            } finally {
                // 아래 검증 로직을 할 필요가 있는건가?
//                String roomIdFromJWT = jwtTokenProvider.getChatRoomIdFromJWT(accessToken);
//                String roomIdFromEndpoint = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
//                if (!roomIdFromJWT.equals(roomIdFromEndpoint)) {
//                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
//                }
                return message;
            }
            // 아래는 삭제 예정

//            // jwt 토큰이 있는지 검증
//            jwtTokenProvider.validateReadWriteToken(accessor.getFirstNativeHeader("readWriteToken"));
//            // jwt 토큰이 현재 채팅방에서 유효한 토큰인지 검증 (=chatRoomId 값을 통해서)
//            String roomIdFromJWT = jwtTokenProvider.getChatRoomIdFromJWT(accessor.getFirstNativeHeader("readWriteToken"));
//            String roomIdFromEndpoint = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
//            if (!roomIdFromJWT.equals(roomIdFromEndpoint)) {
//                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
//            }
        }
        return message;
    }

    static class HttpHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            // cookie값으로 사용자 정보 얻기 -> database 뒤져서 참여가능한 채팅방인지 검사하기
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

        }
    }
}
