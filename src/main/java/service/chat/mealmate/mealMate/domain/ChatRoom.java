package service.chat.mealmate.mealMate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;
import service.chat.mealmate.mealMate.domain.vote.Vote;

import javax.persistence.*;
import java.time.LocalDate;
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
    private LocalDateTime openedAt;
    private LocalDateTime lockedAt = null;
    private LocalDateTime expectedClosedAt = null;
    private LocalDateTime closedAt = null;
    private short maxPersonnel;
    // 낙관적 락을 위해 도입
    private short currentPersonnel = 0;
    @Version
    private Integer version = 1;
    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<ChatPeriod> chatPeriodList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    private List<MealMate> mealMateList = new ArrayList<>();

    @OneToMany(mappedBy = "chatRoom")
    private List<Vote> voteList = new ArrayList<>();
    public ChatRoom(String chatRoomId, String title, LocalDateTime openedAt, short maxPersonnel) {
        this.chatRoomId = chatRoomId;
        this.title = title;
        this.openedAt = openedAt;
        this.expectedClosedAt = LocalDateTime.now().plusDays(1);// 24시간 뒤의 시간
        this.maxPersonnel = maxPersonnel;
        for (int i = 0; i < 3; i++) {
            chatPeriodList.add(new ChatPeriod(0, 0, 0, 0, this));
        }
    }
    public short addPersonnel() {
        if (++this.currentPersonnel == this.maxPersonnel) lock(LocalDateTime.now());
        return this.currentPersonnel;
    }
    public void addChatPeriod(int startHour, int startMinute, int endHour, int endMinute, boolean immediately) {
        if (this.lockedAt == null)
            throw new RuntimeException("채팅방이 잠금상태가 아닙니다.");
        // chatPeriodList fetchType이 LAZY이면 아래 구문에서 문제생김. list를 순회하는데 엔터티 안불러옴;
        ChatPeriod chatPeriod = this.chatPeriodList.stream()
                // 예약을 덮어쓰기 하는 건 불가 // 삭제 예약을 덮어쓰기 하는 건 가능
                .filter(i -> (i.deleted.equals(true) && i.reserved.equals(false)) || i.reservedDeleted.equals(true))
                .findAny()
                .orElseThrow(() -> new RuntimeException("채팅시간대를 더 이상 추가할 수 없습니다."));
        // cascade update
        if (immediately)
            chatPeriod.update(startHour, startMinute, endHour, endMinute);
        else
            chatPeriod.reservedUpdate(startHour, startMinute, endHour, endMinute);
    }
    public void updateChatPeriod(Long chatPeriodId, int startHour, int startMinute, int endHour, int endMinute, boolean immediately) {
        if (this.lockedAt == null)
            throw new RuntimeException("채팅방이 잠금상태가 아닙니다.");
        ChatPeriod chatPeriod = this.chatPeriodList.stream()
                // 예약을 덮어쓰기 하는 거 가능
                .filter(i -> i.deleted == false && i.getChatPeriodId() == chatPeriodId)
                .findAny()
                .orElseThrow(() -> new RuntimeException("해당 채팅 시간대를 찾을 수 없습니다."));
        if (immediately)
            chatPeriod.update(startHour, startMinute, endHour, endMinute);
        else
            chatPeriod.reservedUpdate(startHour, startMinute, endHour, endMinute);
    }

    public void updateChatRoomTitle(String newTitle) {
        if (this.lockedAt == null)
            throw new RuntimeException("채팅방이 잠금상태가 아닙니다.");
        this.title = newTitle;
    }
    public void deleteChatPeriod(Long chatPeriodId, boolean immediately) {
        if (this.lockedAt == null) throw new RuntimeException("채팅방이 잠금상태가 아닙니다.");
        // chatperiod를 Map으로 바꾸는게 좋을 듯?
        Stream<ChatPeriod> chatPeriodStream = this.chatPeriodList.stream().filter(i -> i.deleted == false);
        long activeChatPeriodCount = chatPeriodStream.count();
        if (activeChatPeriodCount == 1)  throw new RuntimeException("채팅 시간대를 더 이상 지울 수 없습니다");
        ChatPeriod chatPeriod = chatPeriodStream
                // 예약을 덮어쓰기 하는 거 가능
                .filter(i -> i.deleted == false && i.getChatPeriodId() == chatPeriodId)
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
    public void lock(LocalDateTime lockedAt) {
        // 종료 예상 일자는 필요해 보여서 넣어뒀는데, 아직 어떻게 활용 할 지는 모르겠음
        this.expectedClosedAt = LocalDateTime.of(9999, 12, 31, 0, 0);
        // this.expectedClosedAt = LocalDateTime.MAX; <- 년도가 비정상적으로 큰 값을 생성함;;
        this.lockedAt = lockedAt;
        addChatPeriod(lockedAt.getHour(), lockedAt.getMinute(), lockedAt.getHour() + 1, lockedAt.getMinute(), true);
    }
    public void close(LocalDateTime closedAt) {
        for (MealMate mealMate : mealMateList) {
            // 객체지향적인 코드 // 비효율적인 쿼리
            if (mealMate.isActive()) mealMate.leave(closedAt);
        }
        this.closedAt = closedAt;
    }
    public ChatPeriod findMatchedChatPeriod(LocalTime localTime) {
        if (this.lockedAt == null) throw new RuntimeException("비잠금상태의 채팅방에는 임시시간대만 있습니다");
        return chatPeriodList.stream().filter(chatPeriod -> {
            LocalTime startTime = chatPeriod.getStartTime();
            LocalTime endTime = chatPeriod.getEndTime();
            return startTime.isBefore(localTime) && endTime.isAfter(localTime);
        }).findAny().orElseThrow(() -> new RuntimeException("적절한 채팅 시간대를 찾을 수 없습니다"));
    }

    public ChatPeriod findMatchedChatPeriod(Long chatPeriodId) {
        for (ChatPeriod chatPeriod : chatPeriodList)
            if (chatPeriodId.equals(chatPeriod.getChatPeriodId())) return chatPeriod;
        throw new RuntimeException("적절한 채팅 시간대를 찾을 수 없습니다.");
    }

    public LocalDateTime findMatchedChatPeriodEndTime(LocalDateTime localDateTime) {
        if (this.lockedAt == null) {
            if (this.expectedClosedAt.isAfter(localDateTime))
                return this.expectedClosedAt;
            else
                return null;
        } else {
            LocalTime localTime = localDateTime.toLocalTime();
            ChatPeriod matchedChatPeriod = chatPeriodList.stream().filter(chatPeriod -> {
                LocalTime startTime = chatPeriod.getStartTime();
                LocalTime endTime = chatPeriod.getEndTime();
                return startTime.isBefore(localTime) && endTime.isAfter(localTime);
            }).findAny().orElse(null);
            if (matchedChatPeriod == null)
                return null;
            else
                return LocalDateTime.of(LocalDate.now(), matchedChatPeriod.getEndTime());
        }
    }
}
