package service.chat.mealmate.mileageHistory.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable @NoArgsConstructor @Getter
public class Mileage {
    private Integer appendValue;
    private Integer currentMileage;

    public Mileage createMileage(int appendValue) {
        Mileage mileage = new Mileage(appendValue);
        mileage.currentMileage += this.currentMileage;
        return mileage;
    }

    public Mileage(Integer initValue) {
        this.appendValue = initValue;
        this.currentMileage = 0 + appendValue;
    }

}
