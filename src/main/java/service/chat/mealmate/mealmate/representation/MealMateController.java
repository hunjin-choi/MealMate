package service.chat.mealmate.mealmate.representation;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import websocket.spring_websocket.mealmate.service.MealMateService;

@RestController
@RequestMapping("/mealmate")
@RequiredArgsConstructor
public class MealMateController {
    private final MealMateService mealMateService;
    @GetMapping("/confirm")
    public void confirm(Long senderId, Long receiverId) {
        String confirmMessage = "---";
        mealMateService.confirm(senderId, confirmMessage);
    }
}
