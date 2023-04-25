package service.chat.mealmate.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.mileage.domain.MileageHistory;
import service.chat.mealmate.order.domain.Orders;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class Member {
    @Id() @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    private String name;
    private String nickname;

    @OneToMany(mappedBy = "member")
    private List<MileageHistory> mileageHistoryList = new ArrayList<>();

    @OneToMany(mappedBy = "giver")
    private List<MealMate> mealMate = new ArrayList<>();

    @OneToMany(mappedBy = "member")
    private List<Orders> ordersList = new ArrayList<>();

    public Member(String name, String nickname) {
        this.name = name;
        this.nickname = nickname;
    }
}
