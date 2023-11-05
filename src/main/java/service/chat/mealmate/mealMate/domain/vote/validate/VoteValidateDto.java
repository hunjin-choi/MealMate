package service.chat.mealmate.mealMate.domain.vote.validate;

import lombok.Data;
import service.chat.mealmate.mealMate.dto.ChatPeriodDto;

import java.time.LocalTime;

@Data
public class VoteValidateDto {
    // time-info need
    private Integer startHour;
    private Integer startMinute;
    private Integer endHour;
    private Integer endMinute;
    // title
    private String title;
    //
    private Boolean lock;
    public VoteValidateDto() {
    }

    public static VoteValidateDto of(ChatPeriodDto chatPeriodDto) {
        VoteValidateDto voteValidateDto = new VoteValidateDto();
        LocalTime startTime = chatPeriodDto.getStartTime();
        LocalTime endTime = chatPeriodDto.getEndTime();
        voteValidateDto.startHour = startTime.getHour();
        voteValidateDto.startMinute = startTime.getMinute();
        voteValidateDto.endHour = endTime.getHour();
        voteValidateDto.endMinute = endTime.getMinute();

        return voteValidateDto;
    }

    public static VoteValidateDto of(String title) {
        VoteValidateDto voteValidateDto = new VoteValidateDto();
        voteValidateDto.title = title;

        return voteValidateDto;
    }

    public static VoteValidateDto of(Boolean lock) {
        VoteValidateDto voteValidateDto = new VoteValidateDto();
        voteValidateDto.lock = lock;

        return voteValidateDto;
    }
}
