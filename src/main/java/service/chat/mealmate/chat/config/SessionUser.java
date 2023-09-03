package service.chat.mealmate.chat.config;

import lombok.Getter;
import service.chat.mealmate.member.domain.Member;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;

    public SessionUser(Member member) {
        this.name = member.getName();
        this.email = member.getEmail();
    }
}
