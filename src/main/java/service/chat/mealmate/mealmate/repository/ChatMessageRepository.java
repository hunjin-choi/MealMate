package service.chat.mealmate.mealmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.chat.dto.RedisChatMessageDto;
import service.chat.mealmate.mealmate.domain.ChatMessage;
import service.chat.mealmate.mealmate.domain.ChatRoom;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 아래 쿼리 성능 별로 안좋을듯? sub select 사용하는 거 고려해보기
//    @Query("select cm from ChatMessage cm left join cm.mealMate as mm left join mm.giverId on Member.memberId where mm.chatRoomId = :chatRoomId order by cm.date ASC")
    @Query(value = "select new service.chat.mealmate.chat.dto.RedisChatMessageDto(cm.message, cm.date, m.name, mm.chatRoomId) " +
            "from ChatMessage cm left join cm.mealMate mm left join Member m on mm.giverId = m.memberId " +
            "where mm.chatRoom = :chatRoomId order by cm.sentAt ASC", nativeQuery = true)
    public List<RedisChatMessageDto> findAllChatMessage(ChatRoom chatRoom);
}
