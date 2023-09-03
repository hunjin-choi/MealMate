package service.chat.mealmate.mileageHistory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealmate.domain.FeedbackHistory;
import service.chat.mealmate.mealmate.domain.MileageHistoryReferable;
import service.chat.mealmate.member.domain.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity @Builder @NoArgsConstructor @AllArgsConstructor @Getter
public class MileageHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mileageHistoryId;
    @Embedded
    private Mileage mileage;
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private MileageChangeReason changeReason;

    private String changeReasonDetail;
    @ManyToOne
    private Member member;

    @OneToOne()
    @JoinColumn(name = "feedback_history_id")
    private FeedbackHistory feedBackHistory = null;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event = null;

    public MileageHistory(Member member, int mileage, LocalDateTime createdAt, MileageChangeReason changeReason) {
        if (changeReason != MileageChangeReason.INIT) throw new RuntimeException("적절하지 않은 MileageChangeReason 입니다.");
        this.member = member;
        this.changeReason = changeReason;
        this.createdAt = createdAt;
        this.mileage = new Mileage(mileage);
    }
    protected MileageHistory(Mileage mileage, LocalDateTime createdAt, MileageChangeReason changeReason, Member member, MileageHistoryReferable object) {
        this.mileage = mileage;
        this.createdAt = createdAt;
        this.member = member;
        this.changeReason = changeReason;
        if (changeReason == MileageChangeReason.FEEDBACK) {
            this.feedBackHistory = (FeedbackHistory) object; this.event = null;
        } else if (changeReason == MileageChangeReason.EVENT) {
            this.feedBackHistory = null; this.event = (Event) object;
        } else { // INIT은 여기로 오게 됩니다
            throw new RuntimeException("적절하지 않은 MileageChangeReason 입니다.");
        }
    }

    public MileageHistory createHistory(Integer unitMileage, MileageChangeReason mileageChangeReason, MileageHistoryReferable object, LocalDateTime date) {
        Mileage mileage = this.mileage.createMileage(unitMileage);
        return new MileageHistory(mileage, date, mileageChangeReason, this.member, object);
    }
}
