package service.chat.mealmate.mealMate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.chat.dto.RedisChatMessageDto;
import service.chat.mealmate.mealMate.domain.ChatMessage;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.member.domain.Member;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 아래 쿼리 성능 별로 안좋을듯? sub select 사용하는 거 고려해보기
//    @Query("select cm from ChatMessage cm left join cm.mealMate as mm left join mm.giverId on Member.memberId where mm.chatRoomId = :chatRoomId order by cm.date ASC")
    @Query(value = "select new service.chat.mealmate.chat.dto.RedisChatMessageDto(cm.message, cm.sentAt, m.loginId, mm.chatRoom.chatRoomId) " +
            "from ChatMessage cm left join cm.mealMate as mm left join mm.member as m " +
            "where mm.chatRoom = :chatRoom order by cm.sentAt ASC")
    public List<RedisChatMessageDto> findAllChatMessage(ChatRoom chatRoom);
}
