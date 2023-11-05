package service.chat.mealmate.member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String loginId = null;
    @Column(nullable = false)
    private String password = null;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;
    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "member_id"))
    private Set<Role> roles = new HashSet<>();

    @Embedded
    private Oauth2Info oauth2Info;

    protected void of (String loginId, String password, String email, String picture, Role role, Oauth2Info oauth2Info) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = loginId;
        this.password = password; // @JsonIgnore @ToString(exclued = "password")
        this.email = email;
        this.createdAt = LocalDateTime.now();
        this.roles.add(role);
        this.oauth2Info = oauth2Info;
    }
    public Member(String loginId, String password, String email, String picture, Role role){
        this.of(loginId, password, email, picture, role, null);
    }

    @Builder
    public Member(String loginId, String password, String email, String picture, Role role, Oauth2Info oauth2Info){
        this.of(loginId, password, email, picture, role, oauth2Info);
    }

    public Member update(String name, String picture){
        this.loginId = name;
        return this;
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MileageHistory> mileageHistoryList = new ArrayList<>();

    public void changeNickname(String nickname) {
        this.loginId = nickname;
    }
}
