package service.chat.mealmate.security.Oauth2;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.domain.Role;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.member.service.MemberService;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;
import service.chat.mealmate.security.Oauth2.OAuthAttributes;
import service.chat.mealmate.security.Oauth2.SessionUser;
import service.chat.mealmate.security.domain.SecurityMember;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final HttpSession httpSession;
    private final MemberService memberService;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest,OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // nameAttributeKey = sub
        // name = "huchoi"

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oauth2User.getAttributes());

        Member member = saveOrUpdate(attributes);
        // httpSession.setAttribute("user", new SessionUser(member));
        Set<Role> roles = member.getRoles();
        Set<String> stringRoles = roles.stream().map(i -> i.name()).collect(Collectors.toSet());
        return new DefaultOAuth2User(buildUserAuthority(stringRoles), attributes.getAttributes(), attributes.getNameAttributeKey());
//        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())), attributes.getAttributes(), attributes.getNameAttributeKey());
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

    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail()).map(entity->entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(null);
        if (member == null) {
            return memberService.signUp(attributes.getName(), "no-password", Role.USER);
        } else {
            member = memberRepository.save(member);
            return member;
        }
    }
}