package service.chat.mealmate.member.dto;

import lombok.Getter;
import service.chat.mealmate.mileageHistory.domain.Mileage;
import service.chat.mealmate.mileageHistory.domain.MileageChangeReason;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;

import java.time.LocalDateTime;

@Getter
public class MileageDto {
    private Mileage mileage;
    private MileageChangeReason mileageChangeReason;
    private LocalDateTime date;

    public MileageDto (MileageHistory mileageHistory) {
        this.mileage = mileageHistory.getMileage();
        this.mileageChangeReason = mileageHistory.getChangeReason();
        this.date = mileageHistory.getCreatedAt();
    }
}
