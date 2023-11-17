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
    private Oauth2Platform oauth2Platform;
    private String oauth2AccountId;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture, Oauth2Platform oauth2Platform, String oauth2AccountId) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.oauth2Platform = Oauth2Platform.GOOGLE;
        this.oauth2AccountId = oauth2AccountId;
    }


    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .oauth2Platform(Oauth2Platform.GOOGLE)
                .oauth2AccountId((String) attributes.get("sub"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}