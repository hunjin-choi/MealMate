package service.chat.mealmate.mileage.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.FeedbackHistory;
import service.chat.mealmate.member.domain.Member;

import java.util.Optional;

@Repository
public interface MileageHistoryRepository extends JpaRepository<MileageHistory, Long> {
    public MileageHistory findFirstByMemberOrderByDateDesc(Member member);
    public Optional<MileageHistory> findFirstByFeedBackHistoryOrderByDateDesc(FeedbackHistory feedbackHistory);
    public Long countAllByMember(Member member);
}
