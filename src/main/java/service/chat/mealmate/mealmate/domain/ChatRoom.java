package service.chat.mealmate.mealmate.domain;

import com.nimbusds.openid.connect.sdk.claims.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChatRoom {
    @Id
    private String chatRoomId;

    private String title;
    private LocalDateTime opened_at;
    private LocalDateTime lockedAt = null;
    private LocalDateTime expectedClosedAt = null;
    private LocalDateTime closedAt = null;
    private short maxPersonnel;
    // 낙관적 락을 위해 도입
    private short currentPersonnel = 0;
    @Version
    private Integer version = 1;
    @OneToMany(mappedBy = "chatRoom") @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<ChatPeriod> chatPeriodList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    private List<MealMate> mealMateList = new ArrayList<>();
    public ChatRoom(String chatRoomId, String title, LocalDateTime openedAt, short maxPersonnel) {
        this.chatRoomId = chatRoomId;
        this.title = title;
        this.opened_at = openedAt;
        this.expectedClosedAt = LocalDateTime.now().plusDays(1);// 24시간 뒤의 시간
        this.maxPersonnel = maxPersonnel;
    }
    public short addPersonnel() {
        if (++this.currentPersonnel == this.maxPersonnel) lock(LocalDateTime.now());
        return this.currentPersonnel;
    }
    public void addChatPeriod(int startHour, int startMinute, int endHour, int endMinute, boolean immediately) {
        if (this.lockedAt == null) throw new RuntimeException("채팅방이 잠금상태가 아닙니다.");
        ChatPeriod chatPeriod = this.chatPeriodList.stream()
                .filter(i -> i.deleted == true && i.reserved == false) // 덮어쓰기 불가
                .findAny()
                .orElseThrow(() -> new RuntimeException("채팅시간대를 더 이상 추가할 수 없습니다."));
        // cascade update
        if (immediately)
            chatPeriod.update(startHour, startMinute, endHour, endMinute);
        else
            chatPeriod.reservedUpdate(startHour, startMinute, endHour, endMinute);
    }
    public void updateChatPeriod(Long chatPeriodId, int startHour, int startMinute, int endHour, int endMinute, boolean immediately) {
        if (this.lockedAt == null) throw new RuntimeException("채팅방이 잠금상태가 아닙니다.");
        ChatPeriod chatPeriod = this.chatPeriodList.stream()
                .filter(i -> i.deleted == false && i.getChatPeriodId() == chatPeriodId) // 덮어쓰기 가능
                .findAny()
                .orElseThrow(() -> new RuntimeException("해당 채팅 시간대를 찾을 수 없습니다."));
        if (immediately)
            chatPeriod.update(startHour, startMinute, endHour, endMinute);
        else
            chatPeriod.reservedUpdate(startHour, startMinute, endHour, endMinute);
    }
    public void deleteChatPeriod(Long chatPeriodId, boolean immediately) {
        if (this.lockedAt == null) throw new RuntimeException("채팅방이 잠금상태가 아닙니다.");
        // chatperiod를 Map으로 바꾸는게 좋을 듯?
        Stream<ChatPeriod> chatPeriodStream = this.chatPeriodList.stream().filter(i -> i.deleted == false);
        long activeChatPeriodCount = chatPeriodStream.count();
        if (activeChatPeriodCount == 1)  throw new RuntimeException("채팅 시간대를 더 이상 지울 수 없습니다");
        ChatPeriod chatPeriod = chatPeriodStream
                .filter(i -> i.deleted == false && i.getChatPeriodId() == chatPeriodId) // 덮어쓰기 가능
                .findAny()
                .orElseThrow(() -> new RuntimeException("적절한 채팅 시간대를 찾을 수 없습니다."));
        if (immediately)
            chatPeriod.softDelete();
        else
            chatPeriod.reservedSoftDelete();
    }
    // 24시간을 넘어가면 문제임
    // 임시채팅시간대에 피드백을 하면 문제임
    // 값을 반환하는 이유는 jwt 만들 때 만료시간 설정할 때 사용하기 위함

    public ChatPeriod getTemporalChatPeriod() {
        if (this.lockedAt != null) throw new RuntimeException("잠금상태의 채팅방에서 임시시간대가 있을 수 없습니다.");
        return new ChatPeriod(opened_at.toLocalTime(), expectedClosedAt.toLocalTime(), this);
    }
    public void lock(LocalDateTime lockedAt) {
        this.expectedClosedAt = LocalDateTime.MAX;
        this.lockedAt = lockedAt;
    }
    public void close(LocalDateTime closedAt) {
        for (MealMate mealMate : mealMateList) {
            // 객체지향적인 코드 // 비효율적인 쿼리
            if (mealMate.isActive()) mealMate.leave(closedAt);
        }
        this.closedAt = closedAt;
    }
    public ChatPeriod findMatchedChatPeriod(LocalTime localTime) {
        if (this.lockedAt == null) return null;
        for (ChatPeriod chatPeriod : chatPeriodList){
            LocalTime startTime = chatPeriod.getStartTime();
            LocalTime endTime = chatPeriod.getEndTime();
            if (startTime.isBefore(localTime) && endTime.isAfter(localTime)) return chatPeriod;
        }
        throw new RuntimeException("적절한 채팅 시간대를 찾을 수 없습니다.");
    }

    public ChatPeriod findMatchedChatPeriod(Long chatPeriodId) {
        if (this.lockedAt == null) return null;
        for (ChatPeriod chatPeriod : chatPeriodList)
            if (chatPeriodId.equals(chatPeriod.getChatPeriodId())) return chatPeriod;
        throw new RuntimeException("적절한 채팅 시간대를 찾을 수 없습니다.");
    }
}
