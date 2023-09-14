package service.chat.mealmate.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.chat.dto.RedisChatMessageDto;
import service.chat.mealmate.chat.dto.RedisChatRoom;
import service.chat.mealmate.chat.dto.LoginInfo;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.security.ChatPeriodCheck;
import service.chat.mealmate.security.domain.SecurityMember;
import service.chat.mealmate.security.jwt.JwtTokenProvider;
import service.chat.mealmate.mealMate.domain.MealMate;
import service.chat.mealmate.mealMate.repository.ChatMessageRepository;
import service.chat.mealmate.mealMate.repository.ChatRoomRepository;
import service.chat.mealmate.mealMate.repository.MealMateRepository;
import service.chat.mealmate.mealMate.service.MealMateService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
@ControllerAdvice
public class RedisChatRoomController {
    private final RedisChatRoomRepository redisChatRoomRepository;
    private final MealMateRepository mealMateRepository;
    private final MealMateService mealmateService;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    // 채팅 리스트 화면
    @GetMapping("/room")
    public String rooms(Model model) {
        return "/chat/room";
    }
    // 모든 채팅방 목록 반환
    @GetMapping("/rooms")
    @ResponseBody
    public List<RedisChatRoom> room() {
        return redisChatRoomRepository.findAllRoom();
    }

    @GetMapping("/myRoom")
    @ResponseBody
    public RedisChatRoom myRoom() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SecurityMember principal = (SecurityMember) auth.getPrincipal();
        MealMate mealMate = mealMateRepository.findActivatedBy(principal.getMemberId())
                .orElse(null);
        if (mealMate == null) return null;
        String chatRoomId = mealMate.getChatRoom().getChatRoomId();
        return redisChatRoomRepository.findRoomById(chatRoomId);
    }
    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public RedisChatRoom createRoom(@RequestParam String chatRoomTitle) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        RedisChatRoom chatRoom = redisChatRoomRepository.createChatRoom(chatRoomTitle);
        String chatRoomId = chatRoom.getRoomId();
        MealMate mealMate = mealmateService.createAndJoin(memberId, chatRoomId, chatRoomTitle);
        principal.setChatInfo(mealMate.getMealMateId(), chatRoomId, mealMate.findMatchedChatPeriodEndTime(LocalDateTime.now()));
        return chatRoom;
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{chatRoomId}")
    public String joinRoom(Model model, @PathVariable String chatRoomId) {
        SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long memberId = principal.getMemberId();
        MealMate mealMate = mealmateService.join(memberId, chatRoomId);
        principal.setChatInfo(mealMate.getMealMateId(), chatRoomId, mealMate.findMatchedChatPeriodEndTime(LocalDateTime.now()));
        model.addAttribute("roomId", chatRoomId);
        return "/chat/roomdetail";
    }
    @GetMapping("/room/messages/{roomId}")
    @ResponseBody
    public List<RedisChatMessageDto> roomMessages(@PathVariable String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        return chatMessageRepository.findAllChatMessage(chatRoom);
    }
    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public RedisChatRoom roomInfo(@PathVariable String roomId) {
        return redisChatRoomRepository.findRoomById(roomId);
    }

    @GetMapping("/user/{roomId}")
    @ResponseBody
    @ChatPeriodCheck
    public LoginInfo startChat(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @PathVariable("roomId") String roomId) {
        SecurityMember securityMember = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new LoginInfo(securityMember.getUsername(), "token", "token", "token");
    }

}