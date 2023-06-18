package service.chat.mealmate.mealmate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import service.chat.mealmate.utils.DateUtil;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity @Table(uniqueConstraints = {
        // table의 column 이름
        @UniqueConstraint(name = "unique_index", columnNames = {"mealmate_id", "giver_id", "receiver_id"})
})@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MealMate implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mealmate_id")
    private Long mealmateId;

    private Integer mileagePerMealmate = 0;


    @Temporal(value = TemporalType.TIMESTAMP)
    private Date connectDate;

    // disconnect '예정' 날짜
    // 정해진 시간이 지나도록 disconnect 버튼을 클릭하지 않는 사람은 어떻게 처리?
    // 스케쥴러 돌려야 하나?
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date expectedDisconnectDate;
    // 실제 disconnect 버튼을 클릭한 날짜 <- isActive 컬럼보다는 이게 더 많은 정보를 포함하기에 확장성이 있음
    @Temporal(value = TemporalType.TIMESTAMP)
    private Date actualDisconnectDate = null;
    @Column(name = "giver_id")
    private String giverId;

    // Mealmate에서 ManyToOne이 두 개라도, User에도 OneToMany가 두 개일 필요는 없다.
//    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "RECEIVER")
    @Column(name = "receiver_id")
    private String receiverId;

    @Column(name = "chat_room_id")
    private String chatRoomId;

    @OneToMany(mappedBy = "mealMate")
    private List<FeedbackHistory> feedbackHistoryList;

//    @Getter(value = AccessLevel.PROTECTED)
    @OneToMany(mappedBy = "mealMate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatPeriod> chatPeriodList = new ArrayList<>();

    @OneToMany(mappedBy = "mealMate", cascade = CascadeType.ALL, orphanRemoval = true)
//    @Getter(value = AccessLevel.PROTECTED)
    private List<ChatMessage> chatMessageList = new ArrayList<>();
//    @OneToMany(mappedBy = "mealmate")
//    private List<ChatMessage> chatMessageList = new ArrayList<>();
//    @Transient
//    private int period = 14;

    public MealMate(String giverId, String receiverId, Date connectDate, String chatRoomId) {
        this.giverId = giverId;
        this.receiverId = receiverId;
        this.connectDate = connectDate;
        Date now = DateUtil.getNow();
        this.expectedDisconnectDate = DateUtil.addDaysToDate(now, 14);
        this.chatRoomId = chatRoomId;
    }

    public void connect(Date connectDate, String receiverId) {
        this.connectDate = connectDate;
        this.receiverId = receiverId;
    }
    public void disconnect(Date disconnectDate) {
        this.actualDisconnectDate = disconnectDate;
    }

    public ChatPeriod addChatPeriod(int startHour, int startMinute, int endHour, int endMinute) {
        if (this.chatPeriodList.size() >= 3) throw new RuntimeException("채팅시간대를 더 이상 추가할 수 없습니다");
        ChatPeriod chatPeriod = new ChatPeriod(startHour, startMinute, endHour, endMinute, this);
        this.chatPeriodList.add(chatPeriod);
        return chatPeriod;
    }

    public ChatPeriod addTempChatPeriod() {
        Date now = DateUtil.getNow();
        int hour = DateUtil.getHour(now);
        int minute = DateUtil.getMinute(now);
        // 24를 넘어가면? 어짜피 심야시간에 채팅 안열어줄 것
        Date expiredAt = new Date(now.getTime() + 60 * 60 * 1000L);
        int expiredHour = DateUtil.getHour(expiredAt);
        int expiredMinute = DateUtil.getMinute(expiredAt);
        return addChatPeriod(hour, minute, expiredHour, expiredMinute);
    }

    public void deleteChatPeriod(Long chatPeriodId) {
        // chatperiod를 Map으로 바꾸는게 좋을 듯?
        if (this.chatPeriodList.size() == 1) {
            throw new RuntimeException("채팅 시간대를 더 이상 지울 수 없습니다");
        }
        // delete 로직 작성
        for (ChatPeriod chatPeriod : chatPeriodList) {
            if (chatPeriod.getChatPeriodId() == chatPeriodId) {
                chatPeriodList.remove(chatPeriod);
            }
        }
    }
    public FeedbackHistory confirm(String feedbackMention, Date feedbackDate, int feedbackMileage) {
        FeedbackHistory feedbackHistory = new FeedbackHistory(feedbackMention, feedbackDate, feedbackMileage, this);
        this.mileagePerMealmate += feedbackHistory.getMileagePerFeedback();
        for (ChatPeriod chatPeriod : chatPeriodList) {
            Integer minutes = null;
            if ((minutes = chatPeriod.calculateRemainPeriod(DateUtil.getNow())) != null) {
                chatPeriod.recordFeedbackDate();
                break;
            }
        }
        return feedbackHistory;
    }

    public void addChatMessage(String message) {
        ChatMessage chatMessage = new ChatMessage(message, DateUtil.getNow(), this);
        this.chatMessageList.add(chatMessage);
    }

    public ChatPeriod findTempChatPeriod() {
        return this.getChatPeriodList().get(0);
    }
}
