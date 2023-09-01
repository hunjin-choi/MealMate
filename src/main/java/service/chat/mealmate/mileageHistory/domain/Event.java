package service.chat.mealmate.mileageHistory.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import service.chat.mealmate.mealmate.domain.MileageHistoryReferable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event implements MileageHistoryReferable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    private String name;
    private String description;
    private int mileagePerEvent;

}
