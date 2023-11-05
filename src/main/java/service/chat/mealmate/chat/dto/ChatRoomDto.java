package service.chat.mealmate.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomDto {
    private String roomId;
    private String name;
}
