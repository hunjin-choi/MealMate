package service.chat.mealmate.mealmate.representation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.chat.mealmate.mealmate.service.MealmateService;

@RestController
@RequestMapping("/mealmate")
@RequiredArgsConstructor
public class MealMateController {
    private final MealmateService mealMateService;
    @GetMapping("/confirm")
    public void confirm(Long senderId, Long receiverId) {
        String confirmMessage = "---";
    }
}
