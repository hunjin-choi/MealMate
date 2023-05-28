package service.chat.mealmate.mealmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.ChatPeriod;

@Repository
public interface ChatPeriodRepository extends JpaRepository<ChatPeriod, Long> {

}
