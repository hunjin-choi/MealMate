package service.chat.mealmate.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.domain.Role;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.security.domain.SecurityMember;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional // 스프링 예제 참고
    // 여기서 반환되는 UserDetails 는 어디로 이동하는지?
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails userDetails = memberRepository.findByLoginId(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
        // [Username=huchoi, Password=[PROTECTED], Enabled=true, AccountNonExpired=true, credentialsNonExpired=true, AccountNonLocked=true, Granted Authorities=[ROLE_USER]]
        return userDetails;
    }
    // 해당하는 User 의 데이터가 존재한다면 UserDetails 객체로 만들어서 리턴
    private User createUserDetails(Member member) {
        Set<Role> roles = member.getRoles();
        Set<String> stringRoles = roles.stream().map(i -> i.name()).collect(Collectors.toSet());
        return new SecurityMember(member, buildUserAuthority(stringRoles));
    }

    private List<GrantedAuthority> buildUserAuthority(Set<String> userRoles) {
        List<GrantedAuthority> authorityList = new ArrayList<>();

        for (String userRole : userRoles) {
            // security는 기본적으로 "ROLE_" prefix 붙임
            if (!userRole.matches("^ROLE_"))
                userRole = "ROLE_" + userRole;
            authorityList.add(new SimpleGrantedAuthority(userRole));
        }

        return authorityList;
    }

}