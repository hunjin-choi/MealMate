package service.chat.mealmate.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.mileage.domain.MileageHistory;
import service.chat.mealmate.order.domain.Orders;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class Member {
    @Id() @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;
    private String name;
    private String nickname;

    @Temporal(value = TemporalType.TIMESTAMP)
    private Date date;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MileageHistory> mileageHistoryList = new ArrayList<>();

//    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<MealMate> mealMateList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Orders> ordersList = new ArrayList<>();

    public Member(String name, String nickname, Date date) {
        this.name = name;
        this.nickname = nickname;
        this.date = date;
    }

}
