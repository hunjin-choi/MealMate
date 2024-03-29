package service.chat.mealmate.mealMate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.chat.dto.ChatRoomDto;
import service.chat.mealmate.mealMate.domain.ChatRoom;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    @Query(value = "select cr.* from chat_room as cr left join meal_mate as mm on cr.chat_room_id = mm.chat_room_id where cr.chat_room_id = :chatRoomId and mm.meal_mate_id = :mealMateId", nativeQuery = true)
    public Optional<ChatRoom> findOneWithMealMate(Long mealMateId, String chatRoomId);

    @Query("select new service.chat.mealmate.chat.dto.ChatRoomDto(cr.chatRoomId, cr.title) from ChatRoom cr")
    public List<ChatRoomDto> findAllChatRoom();

    @Query("select new service.chat.mealmate.chat.dto.ChatRoomDto(cr.chatRoomId, cr.title) " +
            "from ChatRoom cr " +
            "where cr.chatRoomId in (select mm.chatRoom.chatRoomId from MealMate mm where mm.member.memberId = :memberId)")
    public List<ChatRoomDto> findChatRoomBy(Long memberId);
}