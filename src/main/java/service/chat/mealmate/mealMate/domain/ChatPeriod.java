package service.chat.mealmate.mealMate.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SQLDelete(sql = "UPDATE chat_period SET deleted = true WHERE id = ?")
//@Where(clause = "deleted = false")
public class ChatPeriod {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chatPeriodId;
    LocalTime startTime;
    LocalTime endTime;
    Boolean deleted = true; // 삭제 여부 기본값 false

    Boolean reserved = false;
    LocalDateTime reservedDateTime = null;
    LocalTime reservedStartTime = null;
    LocalTime reservedEndTime = null;
    Boolean reservedDeleted = false;
    @Version
    Long version = 1L;
    @ManyToOne(cascade = CascadeType.ALL)
    ChatRoom chatRoom;

    @Value("") @Transient
    static private int maxPeriod = 500000;

    static protected int calculateDiffByMinute(LocalTime lhs, LocalTime rhs) {
        int startMinutes = 60 * lhs.getHour() + lhs.getMinute();
        int endMinutes = 60 * rhs.getHour() + rhs.getMinute();
        int compensateEntMinutes = 60 * (rhs.getHour() + 24) + lhs.getMinute();

        int diff = endMinutes - startMinutes >= 0 ? endMinutes - startMinutes : Integer.MAX_VALUE;
        int compensateDiff = compensateEntMinutes - startMinutes;

        return Math.min(diff, compensateDiff);
    }
    static protected void isLessThanMaxPeriod(int startHour, int startMinute, int endHour, int endMinute) {
        int diff = calculateDiffByMinute(LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));
        if (diff > maxPeriod) throw new RuntimeException("채팅 시간이 너무 길거나 시작시간이 끝나는 시간보다 늦습니다.");
    }
    public ChatPeriod(int startHour, int startMinute, int endHour, int endMinute, ChatRoom chatRoom) {
        isLessThanMaxPeriod(startHour, startMinute, endHour, endMinute);
        this.startTime = LocalTime.of(startHour, startMinute);
        this.endTime = LocalTime.of(endHour, endMinute);
        this.chatRoom = chatRoom;
    }

    public ChatPeriod(LocalTime startTime, LocalTime endTime, ChatRoom chatRoom) {
        isLessThanMaxPeriod(startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());
        this.startTime = startTime;
        this.endTime = endTime;
        this.chatRoom = chatRoom;
    }
    // factoryForTemproal
    // factory
    public static ChatPeriod of(int startHour, int startMinute, int endHour, int endMinute, ChatRoom chatRoom) {
        return new ChatPeriod(startHour, startMinute, endHour, endMinute, chatRoom);
    }

    public static ChatPeriod of(LocalTime startTime, LocalTime endTime, ChatRoom chatRoom) {
        return new ChatPeriod(startTime, endTime, chatRoom);
    }
    public static void validate(int startHour, int startMinutes, int endHour, int endMinute) {
        isLessThanMaxPeriod(startHour, startMinutes, endHour, endMinute);
        if (calculateDiffByMinute(LocalTime.of(startHour, startMinutes), LocalTime.of(endHour, endMinute)) > 50)
            throw new RuntimeException("채팅 시간이 너무 깁니다.");
    }
    public Integer calculateRemainMinute(LocalTime localTime) {
        if (startTime.isBefore(localTime) && endTime.isAfter(localTime)) {
            return calculateDiffByMinute(startTime, localTime);
        }
        else return null;
    }

    public LocalDateTime getExpiredDateTime() {
        LocalDateTime expiredDateTime = this.endTime.atDate(LocalDate.now());
        if (this.startTime.isAfter(this.endTime)) {
            expiredDateTime.plusDays(1);
        }
        return expiredDateTime;
    }
    public void update(LocalTime startTime, LocalTime endTime) {
        isLessThanMaxPeriod(startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());
        this.startTime = startTime;
        this.endTime = endTime;
        this.deleted = false;

        this.reserved = false;
        this.reservedDateTime = null;
        this.reservedStartTime = null;
        this.reservedEndTime = null;
        this.reservedDeleted = false;
    }

    // 예약 시간이 되면 이 메서드를 호출해야함
    public void update(int startHour, int startMinute, int endHour, int endMinute) {
        isLessThanMaxPeriod(startHour, startMinute, endHour, endMinute);
        update(LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));
    }

    public void reservedUpdate(int startHour, int startMinute, int endHour, int endMinute, LocalDateTime reservedDateTime) {
        isLessThanMaxPeriod(startHour, startMinute, endHour, endMinute);

        this.reserved = true;
        this.reservedDateTime = reservedDateTime;
        this.reservedStartTime = LocalTime.of(startHour, startMinute);
        this.reservedEndTime = LocalTime.of(endHour, endMinute);
        this.reservedDeleted = false;
    }
    // 예약 시간이 되면 이 메서드를 호출해야 함
    public void softDelete() {
        this.startTime = null;
        this.endTime = null;
        this.deleted = true;

        this.reserved = false;
        this.reservedDateTime = null;
    }
    public void reservedSoftDelete(LocalDateTime reservedDateTime) {
        this.reserved = true;
        this.reservedDateTime = reservedDateTime;
        this.reservedStartTime = null;
        this.reservedEndTime = null;
        this.reservedDeleted = true;
    }
}
