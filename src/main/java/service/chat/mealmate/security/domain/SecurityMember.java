package service.chat.mealmate.security.domain;

import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import service.chat.mealmate.member.domain.Member;

import java.time.LocalDateTime;
import java.util.List;

public class SecurityMember extends User {
    private Long memberId;
    @Setter
    private LocalDateTime chatExpiredAt = null;

    public SecurityMember(Member member, PasswordEncoder passwordEncoder, List<GrantedAuthority> authorities) {
        // 비밀번호 넣어 줄 때 encoding 하지 않으면 에러 발생함
        super(member.getLoginId(), passwordEncoder.encode(member.getPassword()), authorities);
        this.memberId = member.getMemberId();
    }

    public SecurityMember(Member member, PasswordEncoder passwordEncoder, List<GrantedAuthority> authorities, LocalDateTime chatExpiredAt) {
        super(member.getLoginId(), passwordEncoder.encode(member.getPassword()), authorities);
        this.memberId = member.getMemberId();
        this.chatExpiredAt = chatExpiredAt;
    }
}
