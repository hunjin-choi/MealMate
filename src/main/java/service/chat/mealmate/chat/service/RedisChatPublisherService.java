package service.chat.mealmate.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import service.chat.mealmate.chat.dto.ChatMessageType;
import service.chat.mealmate.chat.dto.RedisChatMessageDto;
import service.chat.mealmate.mealMate.domain.ChatMessage;
import service.chat.mealmate.mealMate.service.MealMateService;

/**
 * 1. Client가 보낸 메시지를 redis에게 저장 & 전달하는 역할을 수행
 * 2. 서버에서 Client에게 보낼 메시지를 저장 & 처리하는 역할을 수행
 */
@Service @RequiredArgsConstructor
public class RedisChatPublisherService {
    private final MealMateService mealMateService;
    private final ChannelTopic channelTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    public void convertAndSend(RedisChatMessageDto message, String loginId, Long mealMateId) {
        // 나중에 수정 필요
        String chatRoomId = message.getRoomId();
        // 로그인 회원 정보로 대화명 설정
        if (loginId == null) loginId = "TempNickname";
        message.setSender(loginId);
        // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
        if (ChatMessageType.ENTER.equals(message.getType())) {
            message.setSender("[알림]");
            message.setMessage(loginId + "님이 입장하셨습니다.");
        }
        ChatMessage chatMessage = mealMateService.saveChatMessage(message.getMessage(), mealMateId, message.getType());
        message.setDate(chatMessage.getSentAt());
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

    public void convertAndSend(String message, String loginId, String roomId, Long mealMateId, ChatMessageType chatMessageType) {
        // 나중에 수정 필요
        // 로그인 회원 정보로 대화명 설정
        if (loginId == null) loginId = "TempNickname";
        RedisChatMessageDto redisChatMessageDto = new RedisChatMessageDto(chatMessageType, roomId, loginId, message);
        // 채팅방 입장시에는 대화명과 메시지를 자동으로 세팅한다.
        ChatMessage chatMessage = mealMateService.saveChatMessage(message, mealMateId, chatMessageType);
        redisChatMessageDto.setDate(chatMessage.getSentAt());
        // Websocket에 발행된 메시지를 redis로 발행(publish)
        redisTemplate.convertAndSend(channelTopic.getTopic(), redisChatMessageDto);
    }
}
