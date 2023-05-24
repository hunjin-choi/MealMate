package service.chat.mealmate.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.chat.mealmate.mealmate.service.MealmateService;

@Service @RequiredArgsConstructor
public class ChatService {
    private final MealmateService mealmateService;
}
