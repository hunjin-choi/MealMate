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
import java.util.ArrayList;
import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;
    private String voteTitle;
    private String content;
    private VoteMethodType voteMethodType;
    private VoteSubject voteSubject;
    private LocalDateTime createdAt;
    private LocalDateTime completedDate;

    // 연관관계 -> 의존관계로 바꾸자
//    @Transient @Autowired https://stackoverflow.com/questions/54014047/can-we-use-autowired-on-an-entity-object-in-spring
//    private VotingMethodStrategy votingMethodStrategy;

    @OneToMany(mappedBy = "vote")
    private List<VotePaper> votePaperList = new ArrayList<>();
    @ManyToOne()
    private ChatRoom chatRoom;
    @OneToOne() @JoinColumn(name = "chat_period_vote")
    private ChatPeriodVote chatPeriodVote;
    @OneToOne() @JoinColumn(name = "title_vote_id")
    private TitleVote titleVote;

    public Vote(String voteTitle, String content, VoteMethodType voteMethodType, ChatRoom chatRoom) {
        this.voteTitle = voteTitle;
        this.content = content;
        this.voteMethodType = voteMethodType;
        this.chatRoom = chatRoom;
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
        this.completedDate = LocalDateTime.now();
    }
}
