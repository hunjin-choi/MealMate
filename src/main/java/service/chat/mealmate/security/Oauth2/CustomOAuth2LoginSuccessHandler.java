package service.chat.mealmate.security.Oauth2;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import service.chat.mealmate.security.domain.SecurityMember;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomOAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException, ServletException, IOException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            // OAuth2 로그인 성공 시의 추가 작업 수행
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            // session에 존재하는 pricipal 객체 변경 작업 진행
            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
            // ((OAuth2AuthenticationToken) authentication).setDetails(new SecurityMember(null, null, null, null));

            // 여기서 필요한 작업을 수행하세요
            // 부모 클래스의 기본 동작 실행 (예: 리다이렉트)
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}