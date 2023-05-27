package service.chat.mealmate.mileageHistory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.dto.MileageDto;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface MileageHistoryRepository extends JpaRepository<MileageHistory, Long> {
    public MileageHistory findFirstByMemberOrderByDateDesc(Member member);
    public Optional<MileageHistory> findFirstByFeedBackHistoryIdOrderByDateDesc(Long feedbackHistoryId);
    public Long countAllByMember(Member member);

    @Query("select new service.chat.mealmate.member.dto.MileageDto(mh) from MileageHistory mh where mh.member = :member order by mh.date ASC")
    public List<MileageDto> dynamicTest(Member member);

    public List<MileageHistory> findByMemberOrderByDateAsc(Member member);

}
