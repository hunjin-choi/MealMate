package service.chat.mealmate.mealMate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.mealMate.domain.ChatPeriod;
import service.chat.mealmate.mealMate.repository.ChatPeriodRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final ChatPeriodRepository chatPeriodRepository;
    @Scheduled(cron = "0 0 1 * * * ")
    public void chatPeriodSchedule() {
        LocalDateTime now = LocalDateTime.now();
        List<ChatPeriod> reservedChatPeriods = chatPeriodRepository.reservedChatPeriodAt(now);
        for (ChatPeriod chatPeriod : reservedChatPeriods) {
            if (chatPeriod.getReservedDeleted() == true) {
                chatPeriod.softDelete();
            } else {
                LocalTime startTime = chatPeriod.getReservedStartTime();
                LocalTime endTime = chatPeriod.getReservedEndTime();
                chatPeriod.update(startTime, endTime);
            }
        }
    }
}
