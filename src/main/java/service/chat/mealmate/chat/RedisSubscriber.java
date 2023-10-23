package service.chat.mealmate.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import service.chat.mealmate.chat.dto.RedisChatMessageDto;
import service.chat.mealmate.mealMate.domain.ChatMessage;
import service.chat.mealmate.mealMate.service.MealMateService;
import service.chat.mealmate.security.domain.SecurityMember;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisSubscriber{
    private final ObjectMapper objectMapper;
    private final SimpMessageSendingOperations messagingTemplate;
    private final MealMateService mealMateService;
    /**
     * Redis에서 메시지가 발행(publish)되면 대기하고 있던 Redis Subscriber가 해당 메시지를 받아 처리한다.
     */
    public void sendMessage(String publishMessage) {
        try {
            // ChatMessage 객채로 맵핑
            RedisChatMessageDto redisChatMessageDto = objectMapper.readValue(publishMessage, RedisChatMessageDto.class);
            System.out.println("chatMessage.getMessage() = " + redisChatMessageDto.getMessage());
//            SecurityMember principal = (SecurityMember) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            ChatMessage chatMessage = mealMateService.saveChatMessage(redisChatMessageDto.getMessage(), principal.getMealMateId());
//            redisChatMessageDto.setDate(chatMessage.getSentAt());
            // 채팅방을 구독한 클라이언트에게 메시지 발송
            messagingTemplate.convertAndSend("/sub/chat/room/" + redisChatMessageDto.getRoomId(), redisChatMessageDto);
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
    }
}