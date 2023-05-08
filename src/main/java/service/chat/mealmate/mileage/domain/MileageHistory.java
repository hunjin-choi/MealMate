package service.chat.mealmate.mileage.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealmate.domain.FeedbackHistory;
import service.chat.mealmate.order.domain.Orders;
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

    @Enumerated(EnumType.STRING)
    private MileageChangeReason mileageChangeReason;

    @ManyToOne
    private Member member;

//    @OneToOne()
    @JoinColumn(name = "feedback_history_id")
    private Long feedBackHistoryId;

//    @OneToOne()
    @JoinColumn(name = "orders_id")
    private Long ordersId;

    public MileageHistory(Mileage mileage, Date date, MileageChangeReason mileageChangeReason, Member member, Long fk) {
        this.mileage = mileage;
        this.date = date;
        this.member = member;
        this.mileageChangeReason = mileageChangeReason;
        if (mileageChangeReason == MileageChangeReason.INIT) {
            this.ordersId = null; this.feedBackHistoryId = null;
        } else if (mileageChangeReason == MileageChangeReason.FEEDBACK) {
            this.ordersId = null; this.feedBackHistoryId = fk;
        } else if (mileageChangeReason == MileageChangeReason.PRODUCT_ORDER) {
            this.ordersId = fk; this.feedBackHistoryId = null;
        } else {
            throw new RuntimeException("");
        }
    }

    public Mileage appendValueAndCreateNewMileage(Long appendValue) {
        return this.mileage.appendValueAndCreateNewMileage(appendValue);
    }
}
