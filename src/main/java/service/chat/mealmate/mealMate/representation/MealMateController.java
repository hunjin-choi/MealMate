package service.chat.mealmate.mealMate.representation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.security.domain.SecurityMember;
import service.chat.mealmate.security.jwt.JwtTokenProvider;
import service.chat.mealmate.mealMate.domain.ChatPeriod;
import service.chat.mealmate.mealMate.domain.FeedbackHistory;
import service.chat.mealmate.mealMate.domain.MealMate;
import service.chat.mealmate.mealMate.dto.ChatPeriodDto;
import service.chat.mealmate.mealMate.dto.FeedbackDto;
import service.chat.mealmate.mealMate.service.MealMateService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/mealmate")
@RequiredArgsConstructor
@ControllerAdvice
public class MealMateController {
    private final MealMateService mealmateService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/addPeriod/{roomId}")
    @ResponseBody
    public void addChatPeriod(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @PathVariable("voteId") Long voteId, @RequestBody ChatPeriodDto chatPeriodDto) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        String readWriteToken = httpRequest.getHeader("readWriteToken");
        String userName = jwtTokenProvider.getUserNameFromJwt(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        mealmateService.addChatPeriod(name, voteId, chatPeriodDto);
    }

    @PostMapping ("/deletePeriod/{roomId}/{voteId}/{chatPeriodId}")
    @ResponseBody
    public void deleteChatPeriod(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @PathVariable("voteId") Long voteId, @PathVariable("chatPeriodId") Long chatPeriodId) throws IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        String readWriteToken = httpRequest.getHeader("readWriteToken");
        String userName = jwtTokenProvider.getUserNameFromJwt(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        mealmateService.deleteChatPeriod(name, voteId, chatPeriodId);
    }

    @PostMapping("/feedback/{roomId}")
    @ResponseBody
    public void addFeedback(HttpServletRequest httpRequest, @PathVariable("roomId") String roomId, @RequestBody FeedbackDto feedbackDto) throws IOException {
        // roomId를 pathVaraiable로 넣지 말고, jwt에서 payload로 넣어 둔 값을 가져오는 식으로 하자
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String senderId = auth.getName();
        SecurityMember principal = (SecurityMember) auth.getPrincipal();
        String readWriteToken = httpRequest.getHeader("readWriteToken");
        String userName = jwtTokenProvider.getUserNameFromJwt(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        String roomIdFromJwt = jwtTokenProvider.getChatRoomIdFromJWT(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        if (!roomIdFromJwt.equals(roomId)) throw new RuntimeException("");
        Long chatPeriodId = jwtTokenProvider.getChatPeriodIdFromJWT(readWriteToken).orElseThrow(() -> new RuntimeException(""));
        // chatPeriod 개수 체크, chatPeriod 겹치지 않는지 체크
        // find mealmate -> add ChatPeriod
        mealmateService.feedback(principal.getMemberId(), roomId, chatPeriodId, feedbackDto);
    }

    @GetMapping("/list")
    public String findMealMates(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<MealMate> mealMateList = mealmateService.getMealMates(name);
        model.addAttribute("mealmateList", mealMateList);
        return "mealmate/mealmateList";
    }
    @GetMapping("/feedback/history/{mealmateId}")
    public String findReceivedFeedbackHistories(Model model, @PathVariable("mealmateId") Long mealmateId) {
        List<FeedbackHistory> feedbackList = mealmateService.getReceivedFeedbackHistories(mealmateId);
        model.addAttribute("feedbackList", feedbackList);
        return "mealmate/feedbackList";
    }

    @GetMapping("/chatPeriod/list")
    @ResponseBody
    public List<ChatPeriodDto> findChatPeriod(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<ChatPeriod> chatPeriodList = mealmateService.getChatPeriods(name);
        return ChatPeriodDto.entityToDtoList(chatPeriodList);
//        model.addAttribute("chatPeriodList", chatPeriodList);
//        return "mealmate/chatPeriodList";
    }
}
