package service.chat.mealmate.mealMate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealMate.domain.ChatPeriod;
import service.chat.mealmate.mealMate.domain.FeedbackHistory;
import service.chat.mealmate.mealMate.domain.MealMate;
import service.chat.mealmate.mealMate.dto.FeedbackHistoryDto;
import service.chat.mealmate.mealMate.dto.FeedbackMealMateDto;
import service.chat.mealmate.member.domain.Member;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FeedbackHistoryRepository extends JpaRepository<FeedbackHistory, Long> {
    @Query("select new service.chat.mealmate.mealMate.dto.FeedbackHistoryDto(m.nickname, fh.feedbackMileage, fh.feedbackMention, fh.feedbackDate, fh.feedBackTime) " +
            "from MealMate as mm " +
                "left join FeedbackHistory as fh on fh.giver.mealMateId = mm.mealMateId " +
                "left join mm.member as m " +
            "where mm.mealMateId = :mealMateId")
    public List<FeedbackHistoryDto> findFeedbackHistoriesBy(Long mealMateId);

    @Query(value = "select new service.chat.mealmate.mealMate.dto.FeedbackMealMateDto(mm.mealMateId, m.loginId, fh.feedBackTime, fh.feedbackMention, fh.feedbackMileage) " +
            "from MealMate as mm " +
                "left join FeedbackHistory as fh on fh.receiver.mealMateId = mm.mealMateId and fh.chatPeriod.chatPeriodId = :chatPeriodId " +
                "left join mm.member as m " +
            "where mm.mealMateId <> :giverId")
    public List<FeedbackMealMateDto> findFeedbackAbleMealMateListAtCurrent(Long giverId, Long chatPeriodId);
}
