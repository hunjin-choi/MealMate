package service.chat.mealmate.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class RedisChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;

    private String roomId;

    public static RedisChatRoom create() {
        RedisChatRoom redisChatRoom = new RedisChatRoom();
        redisChatRoom.roomId = UUID.randomUUID().toString();
        return redisChatRoom;
    }
}