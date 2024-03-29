package service.chat.mealmate.mealMate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealMate.domain.vote.*;
import service.chat.mealmate.member.domain.Member;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(uniqueConstraints = {
        // table의 column 이름
        @UniqueConstraint(name = "unique_index", columnNames = {"chat_room_id", "member_id"})
})@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealMate implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meal_mate_id")
    private Long mealMateId;

    private LocalDateTime joinedAt;

    // disconnect '예정' 날짜
    // 정해진 시간이 지나도록 disconnect 버튼을 클릭하지 않는 사람은 어떻게 처리?
    // 스케쥴러 돌려야 하나?
    private LocalDateTime leavedAt = null;
    // 실제 disconnect 버튼을 클릭한 날짜 <- isActive 컬럼보다는 이게 더 많은 정보를 포함하기에 확장성이 있음

    @ManyToOne()
    @JoinColumn(name = "member_id")
    private Member member;
    @ManyToOne()
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    @OneToMany(mappedBy =  "mealMate")
    private List<VotePaper> votePaperList = new ArrayList<VotePaper>();

    @OneToMany(mappedBy = "mealMate")
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    public MealMate(Member member, ChatRoom chatRoom, LocalDateTime joinedAt) {
        this.member = member;
        this.chatRoom = chatRoom;
        this.joinedAt = joinedAt;
        chatRoom.addPersonnel();
    }
    public void leave(LocalDateTime leavedAt) {
        this.leavedAt = leavedAt;
    }

    public boolean isActive() {
        return this.leavedAt == null;
    }
    public VotePaper createVoteAndVoting(String title, String content, VoteMethodType voteMethodType, VoterStatus voterStatus, ChatRoom chatRoom) {
        Vote vote = Vote.of(title, content, voteMethodType, VoteSubject.ADD_CHAT_PERIOD, chatRoom, LocalDateTime.now());
        // cascade option
        VotePaper votePaper = new VotePaper(vote, this, voterStatus, true);
        return votePaper;
    }
    public VotePaper voting(Vote vote, VoterStatus voterStatus, boolean isCreator) {
        // 중복체크를 voterList를 순회하면서 할 것인지?
        // 중복체크를 이 함수밖인 service 계층에서 select문을 통해 할 것인지?
        // 중복체크를 composite unique index를 통해서 할 것인지?
        if (vote.getCompletedAt() != null)
            throw new RuntimeException("이미 종료된 투표입니다.");
        VotePaper votePaper = new VotePaper(vote, this, voterStatus, isCreator);
        return votePaper;
    }

    public ChatMessage addChatMessage(String message) {
        // chatMessageList에 add
        // chatMessageList를 load 한 후 add 하는 것은 아닐까..?
        // 만약 그렇게 동작한다면 불필요한 load 작업이 생기는 거..
        // [쿼리 로그 확인 결과 불필요한 load 없이 바로 add 함]
        ChatMessage chatMessage = new ChatMessage(message, LocalDateTime.now(), this);
        this.chatMessageList.add(chatMessage);
        return chatMessage;
    }
    public ChatPeriod findMatchedChatPeriod(LocalDateTime localDateTime) {
        return chatRoom.findMatchedChatPeriod(localDateTime);
    }

    public Long findMatchedChatPeriodId(LocalDateTime localDateTime) {
        return chatRoom.findMatchedChatPeriod(localDateTime).getChatPeriodId();
    }
    public LocalDateTime findMatchedChatPeriodEndTime(LocalDateTime localDateTime) {
        return chatRoom.findMatchedChatPeriodEndTime(localDateTime);
    }
}
