package service.chat.mealmate.mealmate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.ChatMessage;
import service.chat.mealmate.mealmate.domain.MealMate;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealMateRepository extends JpaRepository<MealMate, Long> {
    @Query("select mm from MealMate mm where mm.receiverId = :memberId and mm.actualDisconnectDate is null ")
    public Optional<MealMate> findActiveMealmateByReceiverId(String memberId);

    @Query("select mm from MealMate mm where mm.giverId = :giverId and mm.chatRoomId = :chatRoomId and mm.actualDisconnectDate is null ")
    public Optional<MealMate> findMealMateByGiverIdAndChatRoomId(String giverId, String chatRoomId);
    @Query("select mm from MealMate mm where mm.giverId = :memberId and mm.actualDisconnectDate is null and mm.chatRoomId =:roomId")
    public Optional<MealMate> findActiveMealmateByGiverIdAndChatRoomId(String memberId, String roomId);

    public Optional<MealMate> findFirstByChatRoomId(String chatRoomId);
    public Optional<MealMate> findByChatRoomIdAndGiverId(String chatRoomId, String giverId);
    @Query("select cm from ChatMessage cm join cm.mealMate mm where mm.chatRoomId = :chatRoomId and mm.giverId = :giverId")
    public List<ChatMessage> findAllChatMessage(String chatRoomId, String giverId);

    @Query("select cm from ChatMessage cm join cm.mealMate mm where mm.chatRoomId = :chatRoomId and mm.giverId = :giverId")
    public Page<ChatMessage> findChatMessagePageable(String chatRoomId, String giverId, Pageable pageable);

    public Long countByChatRoomId(String chatRoomId);

    public Long countByChatRoomIdAndGiverId(String chatRoomId, String giverId);
}
