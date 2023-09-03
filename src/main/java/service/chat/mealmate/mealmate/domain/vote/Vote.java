package service.chat.mealmate.mealmate.domain.vote;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long voteId;
    private String title;
    private String content;
    private VotingMethodType votingMethodType;
    private LocalDateTime completedDate;
    // 연관관계 -> 의존관계로 바꾸자
//    @Transient @Autowired https://stackoverflow.com/questions/54014047/can-we-use-autowired-on-an-entity-object-in-spring
//    private VotingMethodStrategy votingMethodStrategy;

    @OneToMany(mappedBy = "vote")
    private List<Voter> voterList = new ArrayList<>();
    public Vote(String title, String content, VotingMethodType votingMethodType) {
        this.title = title;
        this.content = content;
        this.votingMethodType = votingMethodType;
    }
    // 의존관계 사용
    public void complete(Long totalMember, VotingMethodStrategy votingMethodStrategy) {
        Long agree = voterList.stream().filter((i) -> i.getVoterStatus() == VoterStatus.AGREE).count();
        Long disagree = voterList.stream().filter((i) -> i.getVoterStatus() == VoterStatus.DISAGREE).count();
        if (!votingMethodStrategy.executable(this.votingMethodType, totalMember, agree, disagree)) throw new RuntimeException("제안이 실행될 조건을 만족하지 못합니다.");
        this.completedDate = LocalDateTime.now();
    }
}
