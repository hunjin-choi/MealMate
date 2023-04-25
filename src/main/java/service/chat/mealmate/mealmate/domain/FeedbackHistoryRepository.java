package service.chat.mealmate.mealmate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackHistoryRepository extends JpaRepository<FeedbackHistory, Long> {
    public FeedbackHistory findFirstByMealMateOrderByFeedBackDateDesc(MealMate mealMate);
    public Long countAllByMealMate(MealMate mealMate);
}
