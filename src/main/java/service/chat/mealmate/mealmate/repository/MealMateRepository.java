package service.chat.mealmate.mealmate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.ChatMessage;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealMateRepository extends JpaRepository<MealMate, Long> {
    @Query("select mm from MealMate mm where mm.receiverId = :memberId and mm.actualDisconnectDate is null ")
    public Optional<MealMate> findActiveMealmateByReceiverId(Long memberId);

    @Query("select mm from MealMate mm where mm.giverId = :memberId and mm.actualDisconnectDate is null ")
    public Optional<MealMate> findActiveMealmateByGiverId(Long memberId);

    public Optional<MealMate> findByChatRoomId(String chatRoomId);
    public Optional<MealMate> findByChatRoomIdAndGiverId(String chatRoomId, Long giverId);
    @Query("select cm from ChatMessage cm join cm.mealMate mm where mm.chatRoomId = :chatRoomId and mm.giverId = :giverId")
    public List<ChatMessage> findAllChatMessage(String chatRoomId, Long giverId);

    @Query("select cm from ChatMessage cm join cm.mealMate mm where mm.chatRoomId = :chatRoomId and mm.giverId = :giverId")
    public Page<ChatMessage> findChatMessagePageable(String chatRoomId, Long giverId, Pageable pageable);
}
