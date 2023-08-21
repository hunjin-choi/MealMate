package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import service.chat.mealmate.utils.DateUtil;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public ChatRoom(String title, LocalDateTime opened_at, short maxPersonnel) {
        this.title = title;
        this.opened_at = opened_at;
        this.expectedClosedAt = ;// 다음날 자정
        this.maxPersonnel = maxPersonnel;
    }
    public short addPersonnel() {
        if (++this.currentPersonnel == this.maxPersonnel) lock(new LocalDateTime);
        return this.currentPersonnel;
    }
    public ChatPeriod addChatPeriod(int startHour, int startMinute, int endHour, int endMinute) {
        if (this.chatPeriodList.size() >= 3) throw new RuntimeException("채팅시간대를 더 이상 추가할 수 없습니다");
        ChatPeriod chatPeriod = new ChatPeriod(startHour, startMinute, endHour, endMinute, this);
        this.chatPeriodList.add(chatPeriod);
        return chatPeriod;
    }

    private ChatPeriod addChatPeriod() {

    }
    // 24시간을 넘어가면 문제임
    // 임시채팅시간대에 피드백을 하면 문제임
    // 값을 반환하는 이유는 jwt 만들 때 만료시간 설정할 때 사용하기 위함
    public ChatPeriod addTempChatPeriod() {
        if (!this.chatPeriodList.isEmpty()) return chatPeriodList.get(0);
        Date now = DateUtil.getNow();
        int hour = DateUtil.getHour(now);
        int minute = DateUtil.getMinute(now);
        Date expiredAt = new Date(now.getTime() + 24 * 60 * 60 * 1000L);
        int expiredHour = DateUtil.getHour(expiredAt);
        int expiredMinute = DateUtil.getMinute(expiredAt);
//        chatPeriodList.clear();
        return addChatPeriod(hour, minute, expiredHour, expiredMinute);
    }
    public void deleteChatPeriod(Long chatPeriodId) {
        if (this.lockedAt == null) throw new RuntimeException("채팅방이 잠금상태가 아닙니다.");
        // chatperiod를 Map으로 바꾸는게 좋을 듯?
        if (this.chatPeriodList.size() == 1) {
            throw new RuntimeException("채팅 시간대를 더 이상 지울 수 없습니다");
        }
        // delete 로직 작성
        for (ChatPeriod chatPeriod : chatPeriodList) {
            if (chatPeriod.getChatPeriodId() == chatPeriodId) {
                chatPeriodList.remove(chatPeriod);
                break;
            }
        }
    }
    public void lock(LocalDateTime lockedAt) {
        this.expectedClosedAt = ; // 2999.12.31 <- infinite time. 재사용.
        this.lockedAt = lockedAt;
    }
    public ChatPeriod findMatchedChatPeriod(LocalTime localTime) {
        if (this.lockedAt == null) return null;
        return this.chatPeriodList.get(0);
    }
}
