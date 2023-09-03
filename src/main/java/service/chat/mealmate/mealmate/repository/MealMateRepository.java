package service.chat.mealmate.mealmate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.ChatMessage;
import service.chat.mealmate.mealmate.domain.ChatPeriod;
import service.chat.mealmate.mealmate.domain.ChatRoom;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealMateRepository extends JpaRepository<MealMate, Long> {
    @Query(value = "select mm from MealMate mm where mm.receiverId = :memberId and mm.actualDisconnectDate is null ", nativeQuery = true)
    public Optional<MealMate> findActiveMealmateByReceiverId(String memberId);

    @Query(value = "select * from meal_mate as mm where mm.member_id = :memberId and mm.leaved_at is null ", nativeQuery = true)
    public Optional<MealMate> findActiveMealmateByGiverId(String memberId);

    public List<MealMate> findByChatRoomAndAndLeavedAtIsNull(ChatRoom chatRoom);
    @Query(value = "select mm from MealMate mm where mm.member_id = :memberId and mm.chatRoomId = :chatRoomId and mm.actualDisconnectDate is null ", nativeQuery = true)
    public Optional<MealMate> findActiveMealMateByGiverIdAndChatRoomId(String memberId, String chatRoomId);

    public Optional<MealMate> findByMemberAndChatRoomAndLeavedAtIsNull(Member member, ChatRoom chatRoom);
    @Query(value = "select mm from MealMate mm where mm.giverId = :receiverId and mm.actualDisconnectDate is null and mm.chatRoomId = :roomId", nativeQuery = true)
    public Optional<MealMate> findActiveMealmateByReceiverIdAndChatRoomId(String receiverId, String roomId);
    public Long countByChatRoomAndAndLeavedAtIsNotNull(ChatRoom chatRoom);
    @Query(value = "select count(mm) from MealMate mm where mm.giverId = :giverId and mm.actualDisconnectDate is null and mm.chatRoomId <> :roomId", nativeQuery = true)
    public Long countMemberAttendOtherMealmate(String giverId, String roomId);
    @Query(value = "select mm from MealMate mm where mm.member_id = :memberId and mm.chat_room_id = :chatRoomId ", nativeQuery = true)
    public Optional<MealMate> findByChatRoomIdAndGiverId(String memberId, String chatRoomId);
    @Query(value = "select cm from ChatMessage cm join cm.mealMate mm where mm.chatRoomId = :chatRoomId and mm.giverId = :giverId", nativeQuery = true)
    public List<ChatMessage> findAllChatMessage(String chatRoomId, String giverId);

    @Query(value = "select cm from ChatMessage cm join cm.mealMate mm where mm.chatRoomId = :chatRoomId and mm.giverId = :giverId", nativeQuery = true)
    public Page<ChatMessage> findChatMessagePageable(String chatRoomId, String giverId, Pageable pageable);

}
