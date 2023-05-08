package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.member.domain.Member;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity @Table(uniqueConstraints = {
        // table의 column 이름
        @UniqueConstraint(name = "unique_index", columnNames = {"MEALMATE_ID", "GIVER", "RECEIVER"})
})@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealMate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MEALMATE_ID")
    private Long mealMateId;

    private Long mealMateMileage;

    private Boolean isActive;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date connectDate;
//    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "GIVER")
    private Long giverId;

    // Mealmate에서 ManyToOne이 두 개라도, User에도 OneToMany가 두 개일 필요는 없다.
//    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "RECEIVER")
    private Long receiverId;

    @OneToMany(mappedBy = "mealMate")
    private List<FeedbackHistory> feedbackHistoryList;

    public MealMate(Long mealMateMileage, Boolean isActive, Long giverId, Long receiverId) {
        this.mealMateMileage = mealMateMileage;
        this.isActive = isActive;
        this.giverId = giverId;
        this.receiverId = receiverId;
    }

    public void disconnect() {
        this.isActive = false;
    }

    public FeedbackHistory recordMileageHistory(Long value, String feedbackMention) {
        this.mealMateMileage += value;
        return new FeedbackHistory(value, feedbackMention, new Date(), this);
    }
}
