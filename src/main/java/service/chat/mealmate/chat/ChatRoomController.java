package service.chat.mealmate.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.chat.config.AppUserRole;
import service.chat.mealmate.chat.dto.ChatRoom;
import service.chat.mealmate.chat.dto.LoginInfo;
import service.chat.mealmate.chat.jwt.JwtTokenProvider;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository;
    private final JwtTokenProvider jwtTokenProvider;
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
    // 특정 채팅방 조회
    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId);
    }

    @GetMapping("/user/{roomId}")
    @ResponseBody
    public LoginInfo getUserInfo(HttpServletResponse httpResponse, @PathVariable("roomId") String roomId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        // 이 name을 이용하여 readonly-jwt 발급조건 만족하는지 검증
        // 이 name을 이용하여 readWrite-jwt 발급조건 만족하는지 검증
        List<AppUserRole> lst = new ArrayList<>();
        lst.add(AppUserRole.ROLE_ADMIN); lst.add(AppUserRole.ROLE_CLIENT);
        String readOnlyJWT = jwtTokenProvider.generateReadOnlyJWT(name, roomId, lst, new Date(new Date().getTime() + 1000L * 60 * 60));
        String readWriteJWT = jwtTokenProvider.generateReadWriteJWT(name, roomId, lst, new Date(new Date().getTime() + 1000L * 60 * 60));
        // 아래 방식처럼 jwt 값 전달하는 방식 안 좋음. 나중에 바꿀 것.
        return LoginInfo.builder().name(name).token(readWriteJWT).build();
    }
}