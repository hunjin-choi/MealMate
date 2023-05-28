package service.chat.mealmate.member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;

import javax.persistence.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED) @Getter
public class Member {
    @Id()
    private String memberId;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    @Column
    private String refreshToken = null;
    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Role role;

    @Builder
    public Member(String memberId, String name, String email, String picture, Role role){
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    public Member update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MileageHistory> mileageHistoryList = new ArrayList<>();

//    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<MealMate> mealMateList = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Orders> ordersList = new ArrayList<>();

    public void changeNickname(String nickname) {
        this.name = nickname;
    }

    public void connectChatRoom(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
