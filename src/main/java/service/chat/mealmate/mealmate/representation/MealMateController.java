package service.chat.mealmate.mealmate.representation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import service.chat.mealmate.mealmate.domain.ChatPeriod;
import service.chat.mealmate.mealmate.domain.FeedbackHistory;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.mealmate.service.MealmateService;
import service.chat.mealmate.member.dto.MileageDto;

import java.util.List;

@Controller
@RequestMapping("/mealmate")
@RequiredArgsConstructor
public class MealMateController {
    private final MealmateService mealMateService;
    @GetMapping("/confirm")
    public void confirm(Long senderId, Long receiverId) {
        String confirmMessage = "---";
    }

    @GetMapping("/list")
    public String findMealmate(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<MealMate> mealMateList = mealMateService.findALlMealmate(name);
        model.addAttribute("mealmateList", mealMateList);
        return "mealmate/mealmateList";
    }
    @GetMapping("/feedback/history/{mealmateId}")
    public String findMileageHistory(Model model, @PathVariable("mealmateId") Long mealmateId) {
        List<FeedbackHistory> feedbackList = mealMateService.findAllFeedbackHistory(mealmateId);
        model.addAttribute("feedbackList", feedbackList);
        return "mealmate/feedbackList";
    }

    @GetMapping("/chatPeriod/list")
    public String findChatPeriod(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        List<ChatPeriod> chatPeriodList = mealMateService.findAllChatPeriod(name);
        model.addAttribute("chatPeriodList", chatPeriodList);
        return "mealmate/chatPeriodList";
    }
}
