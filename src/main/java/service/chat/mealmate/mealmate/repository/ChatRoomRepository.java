package service.chat.mealmate.mealmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.ChatRoom;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Query(value = "select cr.* from chat_room as cr left join meal_mate as mm where cr.chat_roo_id = :chatRoomId and mm.meal_mate_id = :mealMateId", nativeQuery = true)
    public Optional<ChatRoom> findOneWithMealMate(Long mealMateId, String chatRoomId);
}
