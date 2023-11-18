package service.chat.mealmate.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import service.chat.mealmate.security.Oauth2.CustomOAuth2LoginSuccessHandler;
import service.chat.mealmate.security.Oauth2.CustomOAuth2UserService;
import service.chat.mealmate.member.domain.Role;
import service.chat.mealmate.member.service.MemberService;

import java.util.Arrays;

/**
 * Web Security 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberService memberService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler;
    @Value("${spring.freemarker.domain}")
    private String corsDomain;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // 기본값이 on인 csrf 취약점 보안을 해제한다. on으로 설정해도 되나 설정할경우 웹페이지에서 추가처리가 필요함.
                .cors()
                .and() // cors 설정을 허용한다.
                .headers()
                .frameOptions().sameOrigin() // SockJS는 기본적으로 HTML iframe 요소를 통한 전송을 허용하지 않도록 설정되는데 해당 내용을 해제한다.// CORS를 허용한다.
                .and()
                    .authorizeRequests()
                    // .antMatchers("/chat/**").hasRole(Role.USER.name()) // chat으로 시작하는 리소스에 대한 접근 권한 설정
                    .antMatchers("/**").hasRole(Role.USER.name())
                    .anyRequest().permitAll() // 나머지 리소스에 대한 접근 설정
                .and()
                    .formLogin().defaultSuccessUrl("/chat/room", true) // 권한없이 페이지 접근하면 로그인 페이지로 이동한다.
                .and()
                    .oauth2Login()
                    .successHandler(customOAuth2LoginSuccessHandler)
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService);
    }

    /**
     * 테스트를 위해 임시로 유저를 생성
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
        PasswordEncoder encoder = passwordEncoder();

        memberService.signUp("hunjin", encoder.encode("hunjin"), "hunjin@mail.com", Role.USER);
        memberService.signUp("test", encoder.encode("test"), "test@mail.com", Role.USER);
        memberService.signUp("guest", encoder.encode("guest"), "guest@mail.com", Role.GUEST);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList(corsDomain, "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
