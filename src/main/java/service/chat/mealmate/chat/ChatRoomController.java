package service.chat.mealmate.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import service.chat.mealmate.chat.config.AppUserRole;
import service.chat.mealmate.chat.dto.ChatMessageDto;
import service.chat.mealmate.chat.dto.ChatRoom;
import service.chat.mealmate.chat.dto.LoginInfo;
import service.chat.mealmate.chat.jwt.JwtTokenProvider;
import service.chat.mealmate.mealmate.domain.ChatPeriod;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.mealmate.repository.ChatMessageRepository;
import service.chat.mealmate.mealmate.repository.MealMateRepository;
import service.chat.mealmate.mealmate.service.MealmateService;
import service.chat.mealmate.utils.DateUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
@ControllerAdvice
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final MealMateRepository mealMateRepository;
    private final MealmateService mealmateService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatMessageRepository chatMessageRepository;
    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }
    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> room() {
        return chatRoomRepository.findAllRoom();
    }

    @GetMapping("/myRoom")
    @ResponseBody
    public ChatRoom myRoom() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MealMate mealMate = mealMateRepository.findActiveMealmateByGiverId(auth.getName()).orElse(null);
        if (mealMate == null) return null;
        String chatRoomId = mealMate.getChatRoomId();
        return chatRoomRepository.findRoomById(chatRoomId);
    }
    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRoomRepository.createChatRoom(name);
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

    @GetMapping("/room/messages/{roomId}")
    @ResponseBody
    public List<ChatMessageDto> roomMessages(@PathVariable String roomId) {
        return chatMessageRepository.findAllChatMessage(roomId);
    }
    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    @GetMapping("/user/{roomId}")
    @ResponseBody
    @Transactional
    public LoginInfo getUserInfo(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @PathVariable("roomId") String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        // 이미 active 한 mealmate를 가지고 있으면
        Long otherCount = mealMateRepository.countMemberAttendOtherMealmate(name, roomId);
        if (otherCount != 0) throw new RuntimeException("이미 다른 채팅방을 가지고 있습니다");
//        // jwt 토큰이 있는지 검증
//        jwtTokenProvider.validateReadWriteToken(accessor.getFirstNativeHeader("readWriteToken"));
//        // jwt 토큰이 현재 채팅방에서 유효한 토큰인지 검증 (=chatRoomId 값을 통해서)
//        String roomIdFromJWT = jwtTokenProvider.getChatRoomIdFromJWT(accessor.getFirstNativeHeader("readWriteToken"));
//        String roomIdFromEndpoint = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
//        if (!roomIdFromJWT.equals(roomIdFromEndpoint)) {
//            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
//        }
        String readOnlyJWT = null;
        String readWriteJWT = null;
        String chatJWT = null;
        // 이 name을 이용하여 readonly-jwt 발급조건 만족하는지 검증
        // 이 name을 이용하여 readWrite-jwt 발급조건 만족하는지 검증
        List<AppUserRole> lst = new ArrayList<>();
        lst.add(AppUserRole.ROLE_ADMIN); lst.add(AppUserRole.ROLE_CLIENT);
        Long count = mealMateRepository.countByChatRoomId(roomId);
        boolean isChatRoomIdNotInDB = true; // count == 0
        boolean isChatMemberIsJustOne = true; // count == 1
        Long chatPeriodId = null;
        if (count == 0 || count == 1) { // 처음 만든 방이거나, 현재 채팅방에 1명밖에 없음
            if (count == 0) {
                MealMate mealMate = new MealMate(name, null, null, roomId);
                mealMate.addTempChatPeriod();
                mealMateRepository.save(mealMate);
                chatPeriodId = mealMate.findTempChatPeriod().getChatPeriodId();
            } else if (count == 1) {
                // A가 나갔다가 재입장 한 걸수도 있으니까 아래 검증 필요
                // select count(*) from mealmate as mm where mm.giverId = name and mm.roomId = roomId
                count = mealMateRepository.countByChatRoomIdAndGiverId(roomId, name);
                MealMate unconnectedMealmate = mealMateRepository.findFirstByChatRoomId(roomId).orElse(null);
                if (count == 0) {
                    String unconnectedMember = unconnectedMealmate.getGiverId();
                    MealMate mealMate = new MealMate(name, null, null, roomId);
                    mealMate.addTempChatPeriod();
                    chatPeriodId = mealMate.findTempChatPeriod().getChatPeriodId();
                    unconnectedMealmate.connect(DateUtil.getNow(), name);
                    mealMate.connect(DateUtil.getNow(), unconnectedMember);
                    mealMateRepository.save(mealMate);
                    // 이제 하나의 mealMate 쌍이 완성 되었다.
                } else {
                    chatPeriodId = unconnectedMealmate.findTempChatPeriod().getChatPeriodId();
                    // N/A
                }
            }
            // 임시 토근 발급
            readWriteJWT = jwtTokenProvider.generateReadWriteToken(name, roomId, chatPeriodId, lst, new Date(new Date().getTime() + 1000L * 60 * 60));
        } else { // 채팅방에 2명이 꽉 차있음
            // 임시 토큰 발급 안한다
            // 토큰을 요청한 사람이 현재 채팅방의 멤버인지
            MealMate mealMate = mealMateRepository.findActiveMealMateByGiverIdAndChatRoomId(name, roomId).orElse(null);
            Date expiredDate = null;
            if (mealMate != null) {
                // 일단 readOnlyToken 발급
                readOnlyJWT = jwtTokenProvider.generateReadOnlyJWT(name, roomId, lst, new Date(new Date().getTime() + 1000L * 60 * 60));
                // 현재시간이 채팅시간대이면 readWriteToken 발
                List<ChatPeriod> chatPeriodList = mealMate.getChatPeriodList();
                for (ChatPeriod chatPeriod : chatPeriodList) {
                    Integer minutes = null;
                    if ((minutes = chatPeriod.calculateRemainPeriod(DateUtil.getNow())) != null) {
                        expiredDate = DateUtil.addMinute(DateUtil.getNow(), minutes);
                        chatPeriodId = chatPeriod.getChatPeriodId();
                        readWriteJWT = jwtTokenProvider.generateReadWriteToken(name, roomId, chatPeriodId, lst, expiredDate);
                    }
                }
            } else {
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
            }
        }
        chatJWT = readWriteJWT;
        Cookie chatJWTCookie = new Cookie(JwtTokenProvider.CHAT_TOKEN, chatJWT);
        Cookie readWriteCookie = new Cookie("readWriteCookie", readWriteJWT);

        chatJWTCookie.setPath("/"); readWriteCookie.setPath("/"); // 이거 안하면 path=/chat/user 로 설정됨
//        chatJWTCookie.setHttpOnly(true); readWriteCookie.setHttpOnly(true);
//        chatJWTCookie.setSecure(true); readWriteCookie.setSecure(true);
        chatJWTCookie.setMaxAge(3600); readWriteCookie.setMaxAge(3600);
        httpResponse.addCookie(chatJWTCookie);  httpResponse.addCookie(readWriteCookie);
        // 아래 방식처럼 jwt 값 전달하는 방식 안 좋음. 나중에 바꿀 것.
//        return new LoginInfo(name, null, null, null);
        return new LoginInfo(name, readWriteJWT, readWriteJWT, readWriteJWT);
    }
}