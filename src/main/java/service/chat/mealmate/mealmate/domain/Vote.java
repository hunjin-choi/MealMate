package service.chat.mealmate.mealmate.domain;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
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
    @Transient @Autowired
    private VotingMethodStrategy votingMethodStrategy;

    @OneToMany(mappedBy = "vote")
    private List<Voter> voterList = new ArrayList<>();
    public Vote(String title, String content, VotingMethodType votingMethodType) {
        this.title = title;
        this.content = content;
        this.votingMethodType = votingMethodType;
    }
    public void complete(Long totalMember) {
        Long agree = voterList.stream().filter((i) -> i.getVoterStatus() == VoterStatus.AGREE).count();
        Long disagree = voterList.stream().filter((i) -> i.getVoterStatus() == VoterStatus.DISAGREE).count();
        if (!votingMethodStrategy.executable(this.votingMethodType, totalMember, agree, disagree)) throw new RuntimeException("제안이 실행될 조건을 만족하지 못합니다.");
        this.completedDate = LocalDateTime.now();
    }
}
