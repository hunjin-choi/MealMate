package service.chat.mealmate.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.chat.mealmate.mealmate.service.MealMateService;

@Service @RequiredArgsConstructor
public class ChatService {
    private final MealMateService mealmateService;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }
}
