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
    LocalDate reservedDate = null;
    LocalTime reservedStartTime = null;
    LocalTime reservedEndTime = null;
    Boolean reservedDeleted = false;
    @Version
    Long version = 1L;
    @ManyToOne(cascade = CascadeType.ALL)
    ChatRoom chatRoom;

    @Value("") @Transient
    private int maxPeriod = 500000;

    protected int calculateDiffByMinute(LocalTime lhs, LocalTime rhs) {
        int startMinutes = 60 * lhs.getHour() + lhs.getMinute();
        int endMinutes = 60 * rhs.getHour() + rhs.getMinute();
        return endMinutes - startMinutes;
    }
    protected void isLessThanMaxPeriod(int startHour, int startMinute, int endHour, int endMinute) {
        int startMinutes = 60 * startHour + startMinute;
        int endMinutes = 60 * endHour + endMinute;
        int diff = endMinutes - startMinutes;
        if (diff > maxPeriod) throw new RuntimeException("채팅 시간이 너무 깁니다.");
        if (diff < 0) throw new RuntimeException("종료 시간이 시작 시간보다 앞섭니다.");
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
    public Integer calculateRemainMinute(LocalTime localTime) {
        if (startTime.isBefore(localTime) && endTime.isAfter(localTime)) {
            return calculateDiffByMinute(startTime, localTime);
        }
        else return null;
    }
    public void update(LocalTime startTime, LocalTime endTime) {
        isLessThanMaxPeriod(startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());
        this.startTime = startTime;
        this.endTime = endTime;
        this.deleted = false;

        this.reserved = false;
        this.reservedDate = null;
        this.reservedStartTime = null;
        this.reservedEndTime = null;
        this.reservedDeleted = false;
    }

    // 예약 시간이 되면 이 메서드를 호출해야함
    public void update(int startHour, int startMinute, int endHour, int endMinute) {
        isLessThanMaxPeriod(startHour, startMinute, endHour, endMinute);
        update(LocalTime.of(startHour, startMinute), LocalTime.of(endHour, endMinute));
    }

    public void reservedUpdate(int startHour, int startMinute, int endHour, int endMinute) {
        isLessThanMaxPeriod(startHour, startMinute, endHour, endMinute);
        LocalDate now = LocalDate.now();

        this.reserved = true;
        this.reservedDate = now.plusDays(1);
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
        this.reservedDate = null;
    }
    public void reservedSoftDelete() {
        LocalDate now = LocalDate.now();

        this.reserved = true;
        this.reservedDate = now.plusDays(1);;
        this.reservedStartTime = null;
        this.reservedEndTime = null;
        this.reservedDeleted = true;
    }
}
