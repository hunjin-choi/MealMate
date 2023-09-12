package service.chat.mealmate.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.chat.dto.RedisChatRoom;
import service.chat.mealmate.chat.dto.LoginInfo;
import service.chat.mealmate.security.jwt.JwtTokenProvider;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.mealmate.repository.ChatMessageRepository;
import service.chat.mealmate.mealmate.repository.ChatRoomRepository;
import service.chat.mealmate.mealmate.repository.MealMateRepository;
import service.chat.mealmate.mealmate.service.MealMateService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        MealMate mealMate = mealMateRepository.findActivatedBy(auth.getName()).orElse(null);
        if (mealMate == null) return null;
        String chatRoomId = mealMate.getChatRoom().getChatRoomId();
        return redisChatRoomRepository.findRoomById(chatRoomId);
    }
    // 채팅방 생성
    @PostMapping("/room")
    @ResponseBody
    public RedisChatRoom createRoom(@RequestParam String name) {
        return redisChatRoomRepository.createChatRoom(name);
    }
    // 채팅방 입장 화면
    @GetMapping("/room/enter/{roomId}")
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId);
        return "/chat/roomdetail";
    }

//    @GetMapping("/room/messages/{roomId}")
//    @ResponseBody
//    public List<ChatMessageDto> roomMessages(@PathVariable String roomId) {
//        return chatMessageRepository.findAllChatMessage(roomId);
//    }
    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public RedisChatRoom roomInfo(@PathVariable String roomId) {
        return redisChatRoomRepository.findRoomById(roomId);
    }

    @GetMapping("/user/{roomId}")
    @ResponseBody
    @Transactional
    public LoginInfo getUserInfo(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @PathVariable("roomId") String roomId) {
        return new LoginInfo("huchoi", "token", "token", "token");
    }
}