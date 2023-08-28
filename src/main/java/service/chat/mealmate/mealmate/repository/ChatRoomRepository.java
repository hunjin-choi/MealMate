package service.chat.mealmate.mealmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
}
