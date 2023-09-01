package service.chat.mealmate.mealmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.FeedbackHistory;
import service.chat.mealmate.mealmate.domain.MealMate;

import java.util.List;

@Repository
public interface FeedbackHistoryRepository extends JpaRepository<FeedbackHistory, Long> {
    public List<FeedbackHistory> findByReceiverAndOrderByFeedbackDate(MealMate mealMate);
    public Long countAllByMealMate(MealMate mealMate);
}
