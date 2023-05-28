package service.chat.mealmate.mealmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.ChatMessage;
import service.chat.mealmate.mealmate.domain.ChatPeriod;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatPeriod, Long> {
    // 아래 쿼리 성능 별로 안좋을듯? sub select 사용하는 거 고려해보기
    @Query("select cm from ChatMessage cm left join cm.mealMate as mm where mm.chatRoomId = :chatRoomId order by cm.date ASC")
    public List<ChatMessage> findAllChatMessage(String chatRoomId);
}
