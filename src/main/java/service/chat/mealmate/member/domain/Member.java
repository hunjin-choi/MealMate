package service.chat.mealmate.member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
    @Id()
    private String memberId;
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;
    @Column(nullable = false)
    private String email;

    @Column(length = 500)
    private String refreshToken = null;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public Member(String memberId, String name, String email, String picture, Role role){
        this.memberId = memberId;
        this.name = name;
        this.nickname = name;
        this.password = "123"; // @JsonIgnore @ToString(exclued = "password")
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.role = role;
    }

    public Member update(String name, String picture){
        this.name = name;
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
    public void reConnectChatRoom(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public void disconnectChatRoom(String refreshToken) { this.refreshToken = refreshToken; }
}
