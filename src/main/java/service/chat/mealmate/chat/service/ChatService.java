package service.chat.mealmate.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.chat.mealmate.mealmate.service.MealmateService;

@Service @RequiredArgsConstructor
public class ChatService {
    private final MealmateService mealmateService;

    public String getRoomId(String destination) {
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1)
            return destination.substring(lastIndex + 1);
        else
            return "";
    }
}
