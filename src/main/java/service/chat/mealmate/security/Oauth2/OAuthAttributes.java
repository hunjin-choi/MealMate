package service.chat.mealmate.security.Oauth2;

import lombok.Builder;
import lombok.Getter;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.domain.Oauth2Info;
import service.chat.mealmate.member.domain.Oauth2Platform;
import service.chat.mealmate.member.domain.Role;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture){
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public Member toEntity() {
        Oauth2Info oauth2Info = new Oauth2Info(Oauth2Platform.GOOGLE, (String) attributes.get(nameAttributeKey));
        return Member.builder()
                .loginId(null)
                .email(email)
                .picture(picture)
                .role(Role.USER)
                .oauth2Info(oauth2Info)
                .build();
    }
}