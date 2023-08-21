package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.member.domain.Member;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @Table(uniqueConstraints = {
        // table의 column 이름
        @UniqueConstraint(name = "unique_index", columnNames = {"mealmate_id", "giver_id", "receiver_id"})
})@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealMate implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mealmate_id")
    private Long mealmateId;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date joinedAt;

    // disconnect '예정' 날짜
    // 정해진 시간이 지나도록 disconnect 버튼을 클릭하지 않는 사람은 어떻게 처리?
    // 스케쥴러 돌려야 하나?
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date leavedAt = null;
    // 실제 disconnect 버튼을 클릭한 날짜 <- isActive 컬럼보다는 이게 더 많은 정보를 포함하기에 확장성이 있음

    @ManyToOne()
    private Member member;
    @ManyToOne()
    private ChatRoom chatRoom;

    @OneToMany(mappedBy =  "mealMate")
    private List<Voter> voterList = new ArrayList<Voter>();
    public MealMate(Member member, ChatRoom chatRoom, Date joinedAt) {
        this.member = member;
        this.chatRoom = chatRoom;
        this.joinedAt = joinedAt;
        chatRoom.addPersonnel();
    }
    public void leave(Date leaved_at) {
        this.leavedAt = leaved_at;
    }

    public Voter createVoteAndVoting(String title, String content, VoteType voteType, VoterStatus voterStatus) {
        Vote vote = new Vote(title, content, voteType);
        // cascade option
        Voter voter = new Voter(vote, this, voterStatus, true);
        return voter;
    }
    public Voter voting(Vote vote, VoterStatus voterStatus) {
        // 중복체크를 voterList를 순회하면서 할 것인가?
        // 중복체크를 이 함수밖인 service 계층에서 select문을 통해 할 것인가?
        Voter voter = new Voter(vote, this, voterStatus, false);
        return voter;
    }
}
