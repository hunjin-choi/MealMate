package service.chat.mealmate.mileage.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.member.domain.Member;

@Repository
public interface MileageHistoryRepository extends JpaRepository<MileageHistory, Long> {
    public MileageHistory findFirstByMemberOrderByDateDesc(Member member);
}
