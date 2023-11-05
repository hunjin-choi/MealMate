package service.chat.mealmate.mealMate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.mealMate.domain.vote.Vote;
import service.chat.mealmate.mealMate.domain.vote.VoteSubject;
import service.chat.mealmate.mealMate.domain.vote.VoterStatus;
import service.chat.mealmate.mealMate.domain.vote.VotingMethod;
import service.chat.mealmate.mealMate.dto.VoteChatLockDto;
import service.chat.mealmate.mealMate.dto.VoteChatPeriodChangeDto;
import service.chat.mealmate.mealMate.dto.VoteTitleChangeDto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long>{
    @Query(value = "select v.* from vote as v left join chat_room as cr on v.chat_room_id = cr.chat_room_id " +
            "where v.vote_id = :voteId and cr.chat_room_id = :chatRoomId", nativeQuery = true)
    public Optional<Vote> findOneWithChatRoom(Long voteId, String chatRoomId);

    public interface VoteChatPeriodChangeDtoInterface {
        Long getVoteId();
        String getVoteTitle();
        String getContent();
        String getVoteMethodType();
        String getVoteSubject();
        LocalDateTime getCreatedAt();
        LocalDateTime getCompletedAt();
        LocalTime getStartTime();
        LocalTime getEndTime();
        Long getAgree();
        Long getDisagree();
    }
    @Query(value = "select new service.chat.mealmate.mealMate.dto.VoteChatPeriodChangeDto(" +
            "v.voteId, v.voteTitle, v.content, v.voteMethodType, v.voteSubject, v.createdAt, cp.startTime, cp.endTime," +
            "sum(case when vp.voterStatus = :#{#agree} then 1 else 0 end), " +
            "sum(case when vp.voterStatus = :#{#disagree} then 1 else 0 end))" +
            "from VotePaper as vp left join vp.vote as v inner join v.chatPeriodVote as cp " +
            "where v.chatRoom.chatRoomId = :chatRoomId and v.completedDate is null ", nativeQuery = true)
    public List<VoteChatPeriodChangeDtoInterface> findActivatedChatPeriodChangeVote(String chatRoomId, VoterStatus agree, VoterStatus disagree);

    @Query(value = "select " +
            "v.vote_id as voteId, v.vote_title as voteTitle, v.content, v.vote_method_type as voteMethodType, " +
            "v.vote_subject as voteSubject, v.created_at as createdAt, v.completed_at as completedAt, " +
            "cpv.start_time as startTime, cpv.end_time as endTime, " +
            "sum(case when vp.voter_status = :#{#agree.name()} then 1 else 0 end) as agree, " +
            "sum(case when vp.voter_status = :#{#disagree.name()} then 1 else 0 end) as disagree " +
            "from vote_paper as vp " +
            "left join vote v on v.vote_id = vp.vote_id " +
            "left join chat_period_vote cpv on cpv.chat_period_vote_id = v.chat_period_vote " +
            "where v.chat_room_id = :chatRoomId and v.vote_subject in :#{#voteSubjectList.![name()]} " +
            "group by v.vote_id", nativeQuery = true)
    public List<VoteChatPeriodChangeDtoInterface> findAllChatPeriodChangeVote(String chatRoomId, VoterStatus agree, VoterStatus disagree, List<VoteSubject> voteSubjectList);
    @Query("select new service.chat.mealmate.mealMate.dto.VoteTitleChangeDto(" +
            "v.voteId, v.voteTitle, v.content, v.voteMethodType, v.voteSubject, v.createdAt, tv.title) " +
            "from Vote as v left join v.titleVote as tv where v.chatRoom = :chatRoom and v.completedAt is null")
    public List<VoteTitleChangeDto> findActivatedTitleChangeVote(ChatRoom chatRoom);

    @Query("select new service.chat.mealmate.mealMate.dto.VoteTitleChangeDto(" +
            "v.voteId, v.voteTitle, v.content, v.voteMethodType, v.voteSubject, v.createdAt, v.completedAt, tv.title) " +
            "from Vote as v left join v.titleVote as tv where v.chatRoom = :chatRoom")
    public List<VoteTitleChangeDto> findAllTitleChangeVote(ChatRoom chatRoom);

    @Query("select new service.chat.mealmate.mealMate.dto.VoteChatLockDto(" +
            "v.voteId, v.voteTitle, v.content, v.voteMethodType, v.voteSubject, v.createdAt, v.completedAt) " +
            "from Vote as v where v.voteSubject = :voteSubject and v.chatRoom = :chatRoom and v.completedAt is null")
    public List<VoteChatLockDto> findActivatedLockChangeVote(ChatRoom chatRoom, VoteSubject voteSubject);

    @Query("select new service.chat.mealmate.mealMate.dto.VoteChatLockDto(" +
            "v.voteId, v.voteTitle, v.content, v.voteMethodType, v.voteSubject, v.createdAt, v.completedAt) " +
            "from Vote as v where v.voteSubject = :voteSubject and v.chatRoom = :chatRoom")
    public List<VoteChatLockDto> findAllLockChangeVote(ChatRoom chatRoom, VoteSubject voteSubject);
}
