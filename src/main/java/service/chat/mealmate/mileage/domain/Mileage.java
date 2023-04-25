package service.chat.mealmate.mileage.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable @NoArgsConstructor @Getter
public class Mileage {
    private Long appendValue;
    private Long currentMileage;

    public Mileage appendValueAndCreateNewMileage(Long appendValue) {
        return new Mileage(appendValue, currentMileage + appendValue);
    }
    public Mileage(Long appendValue, Long currentMileage) {
        this.appendValue = appendValue;
        this.currentMileage = currentMileage;
    }
}
