package service.chat.mealmate.mileage.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable @NoArgsConstructor @Getter
public class Mileage {
    private Long appendValue;
    private Long currentMileage;

    public Mileage appendValueAndCreateNewMileage(Long appendValue) {
        Mileage mileage = new Mileage(appendValue);
        mileage.currentMileage += this.currentMileage;
        return mileage;
    }

    public Mileage(Long initValue) {
        this.appendValue = initValue;
        this.currentMileage = 0L + appendValue;
    }

}
