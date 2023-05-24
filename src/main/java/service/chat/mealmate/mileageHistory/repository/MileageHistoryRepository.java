package service.chat.mealmate.mileageHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;

import java.util.Optional;

@Repository
public interface MileageHistoryRepository extends JpaRepository<MileageHistory, Long> {
    public MileageHistory findFirstByMemberOrderByDateDesc(Member member);
    public Optional<MileageHistory> findFirstByFeedBackHistoryIdOrderByDateDesc(Long feedbackHistoryId);
    public Long countAllByMember(Member member);
}
