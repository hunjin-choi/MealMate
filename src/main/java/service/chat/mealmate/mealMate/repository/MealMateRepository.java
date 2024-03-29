package service.chat.mealmate.mealMate.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealMate.domain.ChatMessage;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.mealMate.domain.MealMate;
import service.chat.mealmate.mealMate.dto.FeedbackMealMateDto;
import service.chat.mealmate.member.domain.Member;

import java.util.List;
import java.util.Optional;

@Repository
public interface MealMateRepository extends JpaRepository<MealMate, Long> {
    @Query(value = "select mm from meal_mate mm where mm.receiverId = :memberId and mm.leaved_at is null ", nativeQuery = true)
    public Optional<MealMate> findActiveMealmateByReceiverId(String memberId);
    @Query(value = "select count(mm) from meal_mate mm where mm.giverId = :giverId and mm.leaved_at is null and mm.chatRoomId <> :roomId", nativeQuery = true)
    public Long countMemberAttendOtherMealmate(String giverId, String roomId);
    @Query(value = "select * from meal_mate as mm where mm.member_id = :memberId and mm.leaved_at is null ", nativeQuery = true)
    public Optional<MealMate> findActivatedBy(Long memberId);
    @Query(value = "select * from meal_mate as mm where mm.member_id = :memberId and mm.leaved_at is null ", nativeQuery = true)
    public List<MealMate> findAllActiveMealMateBy(Long memberId);
    @Query(value = "select * from meal_mate as mm where mm.chat_room_id = :chatRoomId and mm.leaved_at is null ", nativeQuery = true)
    public List<MealMate> findAllActiveMealMateBy(String chatRoomId);
    @Query(value = "select count(*) from meal_mate as mm where mm.chat_room_id = :chatRoomId and mm.leaved_at is null ", nativeQuery = true)
    public Long countAllActiveMealMateBy(String chatRoomId);
    @Query(value = "select count(*) from meal_mate as mm left join chat_room cr on mm.chat_room_id = cr.chat_room_id where mm.leaved_at is null and cr.chat_room_id = :chatRoomId", nativeQuery = true)
    public Long countAllActivatedBy(String chatRoomId);
    @Query(value = "select * from meal_mate as mm where mm.member_id = :memberId and mm.chat_room_id = :chatRoomId and mm.leaved_at is null limit 1", nativeQuery = true)
    public Optional<MealMate> findOneActivatedCompositeBy(Long memberId, String chatRoomId);

    @Query(value = "select * from meal_mate as mm left join member as m where m.name = :memberName and mm.chat_room_id = :chatRoomId", nativeQuery = true)
    public Optional<MealMate> findOneActivatedWithChatRoomByName(String memberName, String chatRoomId);
    public Optional<MealMate> findByMemberAndChatRoomAndLeavedAtIsNull(Member member, ChatRoom chatRoom);

    @Query(value = "select * from chat_message as cm join cm.mealMate mm where mm.chatRoomId = :chatRoomId and mm.giverId = :giverId", nativeQuery = true)
    public Page<ChatMessage> findChatMessagePageable(String chatRoomId, String giverId, Pageable pageable);

}
