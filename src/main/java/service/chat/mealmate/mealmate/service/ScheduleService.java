package service.chat.mealmate.mealmate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.mealmate.domain.ChatPeriod;
import service.chat.mealmate.mealmate.repository.ChatPeriodRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class ScheduleService {
    private final ChatPeriodRepository chatPeriodRepository;
    @Scheduled(cron = "0 0 1 * * * ")
    public void chatPeriodSchedule() {
        LocalDate today = LocalDate.now();
        List<ChatPeriod> reservedChatPeriods = chatPeriodRepository.reservedChatPeriodAt(today);
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
