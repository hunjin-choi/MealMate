package service.chat.mealmate.mileageHistory.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Embeddable @NoArgsConstructor @Getter
public class Mileage {
    private Integer appendValue;
    private Integer currentMileage;
    protected Mileage(Mileage latestMileage, int appendValue) {
        this.appendValue = appendValue;
        this.currentMileage = latestMileage.currentMileage + appendValue;
    }

    public Mileage(Integer initValue) {
        this.appendValue = initValue;
        this.currentMileage = 0 + appendValue;
    }

    public Mileage createMileage(int appendValue) {
        return new Mileage(this, appendValue);
    }

}
