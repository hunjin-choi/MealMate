package service.chat.mealmate.order.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mileage.domain.MileageHistory;
import service.chat.mealmate.member.domain.Member;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Orders {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ordersId;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

//    @ManyToOne
    private Long memberId;

    @OneToMany(mappedBy = "orders")
    private List<OrderProduct> orderProductList = new ArrayList<OrderProduct>();
}