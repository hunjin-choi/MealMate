package service.chat.mealmate.mealmate.representation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.chat.jwt.JwtTokenProvider;
import service.chat.mealmate.mealmate.domain.ChatPeriod;
import service.chat.mealmate.mealmate.domain.FeedbackHistory;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.mealmate.dto.ChatPeriodDto;
import service.chat.mealmate.mealmate.dto.FeedbackDto;
import service.chat.mealmate.mealmate.service.MealmateService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/mealmate")
@RequiredArgsConstructor
@ControllerAdvice
public class MealMateController {
    private final MealmateService mealmateService;
    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping("/confirm")
    public void confirm(Long senderId, Long receiverId) {
        String confirmMessage = "---";
    }

    @GetMapping("/list")
    public String findMealmate(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<MealMate> mealMateList = mealmateService.findALlMealmate(name);
        model.addAttribute("mealmateList", mealMateList);
        return "mealmate/mealmateList";
    }
    @GetMapping("/feedback/history/{mealmateId}")
    public String findMileageHistory(Model model, @PathVariable("mealmateId") Long mealmateId) {
        List<FeedbackHistory> feedbackList = mealmateService.findAllFeedbackHistory(mealmateId);
        model.addAttribute("feedbackList", feedbackList);
        return "mealmate/feedbackList";
    }

    @GetMapping("/chatPeriod/list")
    @ResponseBody
    public List<ChatPeriodDto> findChatPeriod(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<ChatPeriod> chatPeriodList = mealmateService.findAllChatPeriod(name);
        return ChatPeriodDto.entityToDtoList(chatPeriodList);
//        model.addAttribute("chatPeriodList", chatPeriodList);
//        return "mealmate/chatPeriodList";
    }

    @PostMapping("/addPeriod/{roomId}")
    @ResponseBody
    public void addChatPeriod(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @RequestBody ChatPeriodDto chatPeriodDto) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        String readWriteToken = httpRequest.getHeader("readWriteToken");
        String userName = jwtTokenProvider.getUserNameFromJwt(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        mealmateService.addChatPeriod(name, chatPeriodDto);
    }

    @GetMapping("/deletePeriod/{roomId}/{chatPeriodId}")
    @ResponseBody
    public void deleteChatPeriod(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @PathVariable("chatPeriodId") Long chatPeriodId) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        String readWriteToken = httpRequest.getHeader("readWriteToken");
        String userName = jwtTokenProvider.getUserNameFromJwt(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        mealmateService.deleteChatPeriod(name, chatPeriodId);
    }

    @PostMapping("/feedback/{roomId}")
    @ResponseBody
    public void addFeedback(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @RequestBody FeedbackDto feedbackDto) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        String readWriteToken = httpRequest.getHeader("readWriteToken");
        String userName = jwtTokenProvider.getUserNameFromJwt(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        String roomIdFromJwt = jwtTokenProvider.getChatRoomIdFromJWT(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        if (!roomIdFromJwt.equals(roomId)) throw new RuntimeException("");
        Long chatPeriodId = jwtTokenProvider.getChatPeriodIdFromJWT(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        mealmateService.confirm(name, feedbackDto, roomId, chatPeriodId);
    }
}
