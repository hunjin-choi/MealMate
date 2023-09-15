package service.chat.mealmate.mealMate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.mealMate.domain.vote.Vote;
import service.chat.mealmate.mealMate.dto.VoteChatPeriodChangeDto;
import service.chat.mealmate.mealMate.dto.VoteTitleChangeDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{
    @Query(value = "select v.* from vote as v left join chat_room as cr where v.vote_id = :voteId and cr.chat_room_id = :chatRoomId", nativeQuery = true)
    public Optional<Vote> findOneWithChatRoom(Long voteId, String chatRoomId);

    @Query("")
    public List<VoteTitleChangeDto> findActivatedTitleChangeVote(ChatRoom chatRoom);

    @Query("")
    public List<VoteTitleChangeDto> findAllTitleChangeVote(ChatRoom chatRoom);

    @Query("")
    public List<VoteChatPeriodChangeDto> findActivatedChatPeriodChangeVote(ChatRoom chatRoom);

    @Query("")
    public List<VoteChatPeriodChangeDto> findAllChatPeriodChangeVote(ChatRoom chatRoom);

}
