package service.chat.mealmate.mealMate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealMate.domain.ChatPeriod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatPeriodRepository extends JpaRepository<ChatPeriod, Long> {
    @Query("select cp from ChatPeriod as cp where cp.reserved = true and cp.reservedDateTime = :now")
    public List<ChatPeriod> reservedChatPeriodAt(LocalDateTime now);
}
