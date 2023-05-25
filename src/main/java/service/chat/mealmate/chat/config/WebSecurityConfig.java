package service.chat.mealmate.chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.domain.Role;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.member.service.MemberService;

/**
 * Web Security 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberService memberService;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 기본값이 on인 csrf 취약점 보안을 해제한다. on으로 설정해도 되나 설정할경우 웹페이지에서 추가처리가 필요함.
                .headers()
                .frameOptions().sameOrigin() // SockJS는 기본적으로 HTML iframe 요소를 통한 전송을 허용하지 않도록 설정되는데 해당 내용을 해제한다.
                .and()
                    .authorizeRequests()
                    .antMatchers("/chat/**").hasRole(Role.USER.name()) // chat으로 시작하는 리소스에 대한 접근 권한 설정
                    .anyRequest().permitAll() // 나머지 리소스에 대한 접근 설정
                .and()
                    .formLogin().defaultSuccessUrl("/chat/room") // 권한없이 페이지 접근하면 로그인 페이지로 이동한다.
                .and()
                    .oauth2Login()
                    .defaultSuccessUrl("/chat/room")
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService);
    }

    /**
     * 테스트를 위해 In-Memory에 계정을 임의로 생성한다.
     * 서비스에 사용시에는 DB데이터를 이용하도록 수정이 필요하다.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("happydaddy")
                .password("{noop}1234")
                .roles(Role.USER.name())
                .and()
                .withUser("angrydaddy")
                .password("{noop}1234")
                .roles(Role.USER.name())
                .and()
                .withUser("guest")
                .password("{noop}1234")
                .roles(Role.GUEST.name());
        memberService.signUp("happydaddy", Role.USER);
        memberService.signUp("angrydaddy", Role.USER);
        memberService.signUp("guest", Role.GUEST);

    }
}
