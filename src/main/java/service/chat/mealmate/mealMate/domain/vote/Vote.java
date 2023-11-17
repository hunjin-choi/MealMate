package service.chat.mealmate.mealMate.domain.vote;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealMate.domain.ChatRoom;
import service.chat.mealmate.mealMate.domain.vote.validate.ChatPeriodVote;
import service.chat.mealmate.mealMate.domain.vote.validate.TitleVote;
import service.chat.mealmate.mealMate.domain.vote.validate.VoteValidateDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;
    private String voteTitle;
    private String content;
    @Enumerated(EnumType.STRING)
    private VoteMethodType voteMethodType;
    @Enumerated(EnumType.STRING)
    private VoteSubject voteSubject;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    @Version
    private Long version = 1L; // 두 명 이상이 동시에 투표를 완료를 요청할 수 있기 때문
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL)
    private List<VotePaper> votePaperList = new ArrayList<>();
    @ManyToOne() @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;
    @OneToOne(cascade = CascadeType.ALL) @JoinColumn(name = "chat_period_vote")
    private ChatPeriodVote chatPeriodVote;
    @OneToOne(cascade = CascadeType.ALL) @JoinColumn(name = "title_vote_id")
    private TitleVote titleVote;
    protected Vote(String voteTitle, String content, VoteMethodType voteMethodType, VoteSubject voteSubject, ChatRoom chatRoom, LocalDateTime createdAt) {
        this.voteTitle = voteTitle;
        this.content = content;
        this.voteMethodType = voteMethodType;
        this.voteSubject = voteSubject;
        this.chatRoom = chatRoom;
        this.createdAt = createdAt;
    }

    public static Vote of(String voteTitle, String content, VoteMethodType voteMethodType, VoteSubject voteSubject, ChatRoom chatRoom, LocalDateTime createdAt) {
        Vote vote = new Vote(voteTitle, content, voteMethodType, voteSubject, chatRoom, createdAt);
        return vote;
    }

    public static Vote of(String voteTitle, String content, VoteMethodType voteMethodType, VoteSubject voteSubject, ChatRoom chatRoom, LocalDateTime createdAt, LocalTime startTime, LocalTime endTime) {
        Vote vote = new Vote(voteTitle, content, voteMethodType, voteSubject, chatRoom, createdAt);
        vote.chatPeriodVote = new ChatPeriodVote(startTime, endTime);
        return vote;
    }

    public static Vote of(String voteTitle, String content, VoteMethodType voteMethodType, VoteSubject voteSubject, ChatRoom chatRoom, LocalDateTime createdAt, String chatRoomTitle) {
        Vote vote = new Vote(voteTitle, content, voteMethodType, voteSubject, chatRoom, createdAt);
        vote.titleVote = new TitleVote(chatRoomTitle);
        return vote;
    }
    protected boolean timeInfoNeed() {
        List<VoteSubject> voteSubjects = List.of(new VoteSubject[]{VoteSubject.ADD_CHAT_PERIOD, VoteSubject.UPDATE_CHAT_PERIOD});
        return voteSubjects.contains(this.voteSubject);
    }
    protected boolean titleInfoNeed() {
        List<VoteSubject> voteSubjects = List.of(new VoteSubject[]{VoteSubject.UPDATE_CHAT_ROOM_TITLE});
        return voteSubjects.contains(this.voteSubject);
    }
    // 의존관계 사용
    public void complete(Long totalMember, VotingMethodStrategy votingMethodStrategy, VoteValidateDto dto) {
        if (completedAt != null)
            throw new RuntimeException("이미 완료된 투표입니다.");
        // 값 검증 (배민 결제 검증 하는거 처럼)
        if (timeInfoNeed()) {
            chatPeriodVote.validate(dto.getStartHour(), dto.getStartMinute(), dto.getEndHour(), dto.getEndMinute());
        } else if (titleInfoNeed()) {
            titleVote.validate(dto.getTitle());
        } else {
            // nothing
        }
        // 실행될 조건을 만족하는지 검증
        Long agree = votePaperList.stream().filter((i) -> i.getVoterStatus() == VoterStatus.AGREE).count();
        Long disagree = votePaperList.stream().filter((i) -> i.getVoterStatus() == VoterStatus.DISAGREE).count();
        if (!votingMethodStrategy.executable(this.voteMethodType, totalMember, agree, disagree))
            throw new RuntimeException("제안이 실행될 조건을 만족하지 못합니다.");
        this.completedAt = LocalDateTime.now();
    }
}
