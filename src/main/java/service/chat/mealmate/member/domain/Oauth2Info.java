package service.chat.mealmate.member.domain;

import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable @NoArgsConstructor
public class Oauth2Info {
    private Oauth2Platform oauth2Platform = null;
    private String oauth2AccountId = null;

    public Oauth2Info(Oauth2Platform oauth2Platform, String oauth2AccountId) {
        this.oauth2Platform = oauth2Platform;
        this.oauth2AccountId = oauth2AccountId;
    }
}
