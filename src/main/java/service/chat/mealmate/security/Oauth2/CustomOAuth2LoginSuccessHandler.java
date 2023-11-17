package service.chat.mealmate.security.Oauth2;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.security.domain.SecurityMember;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    private final MemberRepository memberRepository;
//    private final AuthenticationManager authenticationManager;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException, ServletException, IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {

            // OAuth2 로그인 성공 시의 추가 작업 수행
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            // session에 존재하는 pricipal 객체 변경 작업 진행
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List list;
            if (authorities instanceof List)
                list = (List)authorities;
            else
                list = new ArrayList(authorities);
            // ((OAuth2AuthenticationToken) authentication).setDetails(new SecurityMember(null, null, null, null));
            // UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(userName, password);
            OAuthAttributes oAuthAttributes = OAuthAttributes.of(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName(), oauthToken.getPrincipal().getAttributes());
            Member member = memberRepository.findByEmail(oAuthAttributes.getEmail())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

            SecurityMember securityMember = new SecurityMember(member, list);
            SecurityContext securityContext = SecurityContextHolder.getContext();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
            securityContext.setAuthentication(authenticationToken);

            // Create a new session and add the security context.
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            // 여기서 필요한 작업을 수행하세요
            // 부모 클래스의 기본 동작 실행 (예: 리다이렉트)
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}