package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import websocket.spring_websocket.member.domain.Member;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity @Table(uniqueConstraints = {
        // table의 column 이름
        @UniqueConstraint(name = "unique_index", columnNames = {"MEALMATE_ID", "OWNER", "MEMBER"})
})@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealMate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEALMATE_ID")
    private Long mealMateId;

    private Long mealMateMileage;

    private Boolean isActive;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date connectDate;
    @ManyToOne() @JoinColumn(name = "OWNER")
    private Member owner;

    // Mealmate에서 ManyToOwn이 두 개라도, User에도 OneToMany가 두 개일 필요는 없다.
    @ManyToOne @JoinColumn(name = "MEMBER")
    private Member member;

    @OneToMany(mappedBy = "mealMate")
    private List<FeedbackHistory> feedbackHistoryList;

    public MealMate(Long mealMateMileage, Boolean isActive, Member owner, Member member) {
        this.mealMateMileage = mealMateMileage;
        this.isActive = isActive;
        this.owner = owner;
        this.member = member;
    }

    public void disconnect() {
        this.isActive = false;
    }
    public void addMileage(Long value) {
        this.mealMateMileage += value;
    }
}
