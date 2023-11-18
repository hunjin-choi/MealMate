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
import org.springframework.security.web.savedrequest.SavedRequest;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException, ServletException, IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2 로그인 성공 시의 추가 작업 수행
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            // session에 존재하는 pricipal 객체 변경 작업 진행
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            // authorities를 List로 변환
            List authoritiesList;
            if (authorities instanceof List)
                authoritiesList = (List)authorities;
            else
                authoritiesList = new ArrayList(authorities);
            // attribute들을 편리하게 조회하기 위해 OAuth2User 객체로 변환
            OAuthAttributes oAuthAttributes = OAuthAttributes.of(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName(), oauthToken.getPrincipal().getAttributes());
            Member member = memberRepository.findByEmail(oAuthAttributes.getEmail())
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

            // context에 등록할 (CustomUserDetail 객체) securityMember 객체 생성
            SecurityMember securityMember = new SecurityMember(member, authoritiesList);
            // 현재 SecurityContext에 이미 존재하는 Authentication 객체를 제거하고 새로운 Authentication 객체를 추가
            // (구체적으로) OAuth2AuthenticationToken 객체 -> UsernamePasswordAuthenticationToken 객체로 변환
            SecurityContext securityContext = SecurityContextHolder.getContext();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(securityMember, null, securityMember.getAuthorities());
            securityContext.setAuthentication(authenticationToken);

            // request.getSession(boolean) 메서드는 외워두는게 좋다.
            // true일 경우, 세션이 존재하면 현재 세션을 반환하고, 세션이 존재하지 않으면 새로운 세션을 생성한다.
            // false일 경우, 세션이 존재하면 현재 세션을 반환하고, 세션이 존재하지 않으면 null을 반환한다.
            HttpSession session = request.getSession(true);
            // 현재 session에 context를 저장 (이 작업까지해야 비로소 session에 변경된 context가 등록된다)
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
            // 로그인 완료되었을 때 리다이렉션 할 페이지를 지정
            response.sendRedirect("/chat/room");
            // 부모 클래스의 기본 동작 실행
            // 아래 메서드를 호출하여 결과적으로 security config에 defaultSuccessUrl()에 등록한 페이지로 리다이렉션 되는 것을 기대했지만,
            // 생각만큼 잘 되지 않았음 (로그인 성공 후, /chat/room으로 리다이렉션 되지 않음. /로 리다이렉션 되었음..)
            // super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}