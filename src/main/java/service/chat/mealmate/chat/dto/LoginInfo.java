package service.chat.mealmate.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginInfo {
    private String name;
    private String token;
    private String readOnlyToken;
    private String readWriteToken;
    @Builder
    public LoginInfo(String name, String token, String readOnlyToken, String readWriteToken) {
        this.name = name;
        this.token = token;
        this.readOnlyToken = readOnlyToken;
        this.readWriteToken = readWriteToken;
    }
}
