package service.chat.mealmate.mileageHistory.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.member.domain.Member;

import javax.persistence.*;
import java.util.Date;

@Entity @Builder @NoArgsConstructor @AllArgsConstructor @Getter
public class MileageHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mileageHistoryId;

    @Embedded
    private Mileage mileage;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    @Enumerated(EnumType.STRING) // index 걸어야 하는데..
    private MileageChangeReason mileageChangeReason;

    @ManyToOne
    private Member member;

//    @OneToOne()
    @JoinColumn(name = "feedback_history_id")
    private Long feedBackHistoryId = null;

//    @OneToOne()
    @JoinColumn(name = "orders_id")
    private Long ordersId = null;

    @JoinColumn(name = "event_id")
    private Long eventId = null;

    public MileageHistory(Mileage mileage, Date date, MileageChangeReason mileageChangeReason, Member member, Long fk) {
        this.mileage = mileage;
        this.date = date;
        this.member = member;
        this.mileageChangeReason = mileageChangeReason;
        if (mileageChangeReason == MileageChangeReason.INIT) {
            this.ordersId = null; this.feedBackHistoryId = null; this.eventId = null;
        } else if (mileageChangeReason == MileageChangeReason.FEEDBACK) {
            this.ordersId = null; this.feedBackHistoryId = fk; this.eventId = null;
        } else if (mileageChangeReason == MileageChangeReason.PRODUCT_ORDER) {
            this.ordersId = fk; this.feedBackHistoryId = null; this.eventId = null;
        } else if (mileageChangeReason == MileageChangeReason.EVENT) {
            this.ordersId = null; this.feedBackHistoryId = null; this.eventId = fk;
        }else {
            throw new RuntimeException("적절하지 않은 MileageChangeReason 입니다.");
        }
    }

    public MileageHistory createNewHistory(Integer unitMileage, MileageChangeReason mileageChangeReason, Long fk, Date now) {
        Mileage mileage = this.mileage.createNewMileage(unitMileage);
        return new MileageHistory(mileage, now, mileageChangeReason, this.member, fk);
    }
}
