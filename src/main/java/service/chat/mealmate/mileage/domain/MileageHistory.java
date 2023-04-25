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


    @OneToOne() @JoinColumn(name = "feedback_history_id")
    private FeedbackHistory feedBackHistory;

    @OneToOne() @JoinColumn(name = "orders_id")
    private Orders orders;

    public MileageHistory(Mileage mileage, Date date, MileageChangeReason mileageChangeReason, Member member) {
        this.mileage = mileage;
        this.date = date;
        this.mileageChangeReason = mileageChangeReason;
        this.member = member;
    }

}
