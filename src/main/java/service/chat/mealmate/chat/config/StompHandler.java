package service.chat.mealmate.chat.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import service.chat.mealmate.chat.jwt.JwtTokenProvider;
import service.chat.mealmate.chat.service.ChatService;
import service.chat.mealmate.mealmate.domain.ChatPeriod;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.mealmate.repository.MealMateRepository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.utils.DateUtil;

import java.security.Principal;
import java.util.*;

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
//        OAuth2User principal = ((OAuth2AuthenticationToken) user).getPrincipal();
        // websocket 연결시 헤더의 jwt token 검증

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String memberId = user.getName();
            // memberId 값을 통해 데이터베이스 뒤져서 적절한 chatPeriod 찾아서 jwt token 만들기
            Member member = memberRepository.findById(memberId).orElse(null);
//            String chatRoomId = accessor.getFirstNativeHeader("chatRoomId");
            String chatRoomId = accessor.getFirstNativeHeader("chatRoomId"); //jwt에 있는건데 굳이 헤더에 넣을 필요는 없을듯

            Long count = mealMateRepository.countByChatRoomId(chatRoomId);
            Long chatPeriodId = null;
            Date expiredDate = null;
            if (count == 0 || count == 1) { // 처음 만든 방이거나, 현재 채팅방에 1명밖에 없음
                if (count == 0) {
                    MealMate mealMate = new MealMate(memberId, null, null, chatRoomId);
                    chatPeriodId = mealMate.addTempChatPeriod().getChatPeriodId();
                    mealMateRepository.save(mealMate);
                } else if (count == 1) {
                    // A가 나갔다가 재입장 한 걸수도 있으니까 아래 검증 필요
                    // select count(*) from mealmate as mm where mm.giverId = name and mm.roomId = roomId
                    count = mealMateRepository.countByChatRoomIdAndGiverId(chatRoomId, memberId);
                    if (count == 0) {
                        MealMate unconnectedMealmate = mealMateRepository.findFirstByChatRoomId(chatRoomId).orElse(null);
                        String unconnectedMember = unconnectedMealmate.getGiverId();
                        MealMate mealMate = new MealMate(memberId, null, null, chatRoomId);
                        chatPeriodId = mealMate.addTempChatPeriod().getChatPeriodId();
                        unconnectedMealmate.connect(DateUtil.getNow(), memberId);
                        mealMate.connect(DateUtil.getNow(), unconnectedMember);
                        mealMateRepository.save(mealMate);
                        // 이제 하나의 mealMate 쌍이 완성 되었다.
                    } else {
                        MealMate unconnectedMealmate = mealMateRepository.findFirstByChatRoomId(chatRoomId).orElseThrow(() -> new RuntimeException(""));
                        chatPeriodId = unconnectedMealmate.findTempChatPeriod().getChatPeriodId();
                    }
                }
                // 임시 토근 발급
                expiredDate = DateUtil.addHour(DateUtil.getNow(), 1);
            } else { // 채팅방에 2명이 꽉 차있음
                // 임시 토큰 발급 안한다
                // 토큰을 요청한 사람이 현재 채팅방의 멤버인지
                MealMate mealMate = mealMateRepository.findActiveMealMateByGiverIdAndChatRoomId(memberId, chatRoomId).orElseThrow(() -> new RuntimeException(""));
                // 현재시간이 채팅시간대이면 readWriteToken 발
                List<ChatPeriod> chatPeriodList = mealMate.getChatPeriodList();
                for (ChatPeriod chatPeriod : chatPeriodList) {
                    Integer minutes = null;
                    if ((minutes = chatPeriod.calculateRemainPeriod(DateUtil.getNow())) != null) {
                        expiredDate = DateUtil.addMinute(DateUtil.getNow(), minutes);
                        chatPeriodId = chatPeriod.getChatPeriodId();
                    }
                }
            }
            String refreshToken = jwtTokenProvider.generateChatRefreshToken(memberId, chatRoomId, chatPeriodId, expiredDate);
            System.out.println("test\n\n\n");
            String accessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshToken, DateUtil.addMinute(DateUtil.getNow(), 3));

            // accessToken, refreshToken 만들어서 클라이언트에게 전달
            accessor.setHeader("accessToken", accessToken);
            accessor.setHeader("refreshToken", refreshToken);
            accessor.setNativeHeader("accessToken", accessToken);
            accessor.setNativeHeader("refreshToken", refreshToken);
            // refreshToken은 서버에 저장 (member table에 저장)
            member.connectChatRoom(refreshToken);
        }
        // StompCommand.SEND <-> preSend에서의 Send  둘은 다름
        if (StompCommand.SEND.equals(accessor.getCommand())) {
            Long chatPeriodId = null;
            Date expiredDate = null;
            // accessToken 검증
            String accessToken = accessor.getFirstNativeHeader("accessToken");
            String memberId = user.getName();
            Member member = memberRepository.findById(memberId).orElse(null);
            String chatRoomId = jwtTokenProvider.getChatRoomIdFromJWT(accessToken).orElseThrow(() -> new RuntimeException(""));
            // String chatRoomId = accessor.getFirstNativeHeader("chatRoomId"); //jwt에 있는건데 굳이 헤더에 넣을 필요는 없을듯
            String refreshToken = accessor.getFirstNativeHeader("refreshToken");
            String refreshTokenFromDB = member.getRefreshToken();
            // 데이터베이스의 refreshToken 과 비교
            if (refreshTokenFromDB.equals(refreshToken)) {
                // refreshTokenFromDB 유효성 검사
                if (jwtTokenProvider.validateToken(refreshTokenFromDB) == true) {
                    // 유효하면 refreshTokenFromDB claim 값들을 통해 accessToken 발급 // 만약 refreshToken이 1분남았다면?? 이런 경우 고려하기
                    accessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshTokenFromDB, DateUtil.addMinute(DateUtil.getNow(), 3));
                    // accessToken, refreshToken 만들어서 클라이언트에게 전달
                    accessor.setHeader("accessToken", accessToken);
                    accessor.setHeader("refreshToken", refreshToken);
                    // refreshToken은 서버에 저장 (member table에 저장)
                    member.connectChatRoom(refreshToken);
                } else {
                    // 유효하지 않으면 채팅 가능시간대 인지 확인하기
                    // 채팅 가능시간대이면 refreshToken 갱신
                    MealMate mealMate = mealMateRepository.findActiveMealMateByGiverIdAndChatRoomId(memberId, chatRoomId).orElseThrow(() -> new RuntimeException(""));
                    List<ChatPeriod> chatPeriodList = mealMate.getChatPeriodList();
                    for (ChatPeriod chatPeriod : chatPeriodList) {
                        Integer minutes = null;
                        if ((minutes = chatPeriod.calculateRemainPeriod(DateUtil.getNow())) != null) {
                            expiredDate = DateUtil.addMinute(DateUtil.getNow(), minutes);
                            chatPeriodId = chatPeriod.getChatPeriodId();
                        }
                    }
                    if (expiredDate != null) {
                        refreshTokenFromDB = jwtTokenProvider.generateChatRefreshToken(memberId, chatRoomId, chatPeriodId, expiredDate);
                        member.reConnectChatRoom(refreshTokenFromDB);
                        refreshToken = refreshTokenFromDB;
                        jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshTokenFromDB, DateUtil.addMinute(DateUtil.getNow(), 3));
                    } else {
                        refreshTokenFromDB = jwtTokenProvider.generateChatRefreshToken(memberId, chatRoomId, chatPeriodId, expiredDate);
                        member.disconnectChatRoom(refreshTokenFromDB);
                        refreshToken = refreshTokenFromDB;
                        accessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshTokenFromDB, expiredDate);
                        // accessToken, refreshToken 만들어서 클라이언트에게 전달
                        accessor.setHeader("accessToken", accessToken);
                        accessor.setHeader("refreshToken", refreshToken);
                        // refreshToken은 서버에 저장 (member table에 저장)
                        member.connectChatRoom(refreshToken);
                    }
                }
                // accessToken 발급
            } else {
                // refreshTokenFromDB 유효성 검사
                if (jwtTokenProvider.validateToken(refreshTokenFromDB) == true) {
                    // 유효하면 refreshTokenFromDB claim 값들을 통해 accessToken 발급 // 만약 refreshToken이 1분남았다면?? 이런 경우 고려하기
                    accessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshTokenFromDB, DateUtil.addMinute(DateUtil.getNow(), 3));
                    refreshToken = refreshTokenFromDB;
                    // accessToken, refreshToken 만들어서 클라이언트에게 전달
                    accessor.setHeader("accessToken", accessToken);
                    accessor.setHeader("refreshToken", refreshToken);
                    // refreshToken은 서버에 저장 (member table에 저장)
                    member.connectChatRoom(refreshToken);
                } else {
                    // 유효하지 않으면 채팅 가능시간대 인지 확인하기
                    // 채팅 가능시간대이면 refreshToken 갱신
                    MealMate mealMate = mealMateRepository.findActiveMealMateByGiverIdAndChatRoomId(memberId, chatRoomId).orElseThrow(() -> new RuntimeException(""));
                    List<ChatPeriod> chatPeriodList = mealMate.getChatPeriodList();
                    for (ChatPeriod chatPeriod : chatPeriodList) {
                        Integer minutes = null;
                        if ((minutes = chatPeriod.calculateRemainPeriod(DateUtil.getNow())) != null) {
                            expiredDate = DateUtil.addMinute(DateUtil.getNow(), minutes);
                            chatPeriodId = chatPeriod.getChatPeriodId();
                        }
                    }
                    if (expiredDate != null) {
                        refreshTokenFromDB = jwtTokenProvider.generateChatRefreshToken(memberId, chatRoomId, chatPeriodId, expiredDate);
                        member.reConnectChatRoom(refreshTokenFromDB);
                        refreshToken = refreshTokenFromDB;
                        accessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshTokenFromDB, DateUtil.addMinute(DateUtil.getNow(), 3));
                        // accessToken, refreshToken 만들어서 클라이언트에게 전달
                        accessor.setHeader("accessToken", accessToken);
                        accessor.setHeader("refreshToken", refreshToken);
                        // refreshToken은 서버에 저장 (member table에 저장)
                        member.connectChatRoom(refreshToken);
                    } else {
                        refreshTokenFromDB = jwtTokenProvider.generateChatRefreshToken(memberId, chatRoomId, chatPeriodId, expiredDate);
                        member.disconnectChatRoom(refreshTokenFromDB);
                        refreshToken = refreshTokenFromDB;
                        accessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshTokenFromDB, expiredDate);
                        // accessToken, refreshToken 만들어서 클라이언트에게 전달
                        accessor.setHeader("accessToken", accessToken);
                        accessor.setHeader("refreshToken", refreshToken);
                        // refreshToken은 서버에 저장 (member table에 저장)
                        member.disconnectChatRoom(refreshToken);
                    }
                }
            }
            // 아래 검증 로직을 할 필요가 있는건가?
//                String roomIdFromJWT = jwtTokenProvider.getChatRoomIdFromJWT(accessToken);
//                String roomIdFromEndpoint = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
//                if (!roomIdFromJWT.equals(roomIdFromEndpoint)) {
//                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
//                }
        }
        return message;
    }
}
