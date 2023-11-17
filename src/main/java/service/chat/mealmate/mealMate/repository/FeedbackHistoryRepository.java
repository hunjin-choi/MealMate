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
    @Query("select new service.chat.mealmate.mealMate.dto.FeedbackHistoryDto(m2.nickname, fh.feedbackMileage, fh.feedbackMention, fh.feedbackDate, fh.feedBackTime) " +
            "from FeedbackHistory as fh " +
                "left join MealMate as mm1 on fh.receiver.mealMateId = mm1.mealMateId " +
                "left join mm1.member as m1 on mm1.member.memberId = m1.memberId " +
                "left join MealMate as mm2 on fh.giver.mealMateId = mm2.mealMateId " +
                "left join mm2.member as m2 on mm2.member.memberId = m2.memberId " +
                "left join ChatRoom as cr on mm1.chatRoom.chatRoomId = cr.chatRoomId " +
            "where m1.memberId = :memberId " +
            "order by cr.openedAt desc , fh.feedbackDate desc, fh.feedBackTime desc")
    public List<FeedbackHistoryDto> findFeedbackHistoriesBy(Long memberId);

    @Query(value = "select new service.chat.mealmate.mealMate.dto.FeedbackMealMateDto(mm.mealMateId, m.loginId, fh.feedBackTime, fh.feedbackMention, fh.feedbackMileage) " +
            "from MealMate as mm " +
                "left join FeedbackHistory as fh on fh.receiver.mealMateId = mm.mealMateId and fh.chatPeriod.chatPeriodId = :chatPeriodId " +
                "left join mm.member as m on mm.member.memberId = m.memberId " +
            "where mm.mealMateId <> :giverMealMateId")
    public List<FeedbackMealMateDto> findFeedbackAbleMealMateListAtCurrent(Long giverMealMateId, Long chatPeriodId);
}
