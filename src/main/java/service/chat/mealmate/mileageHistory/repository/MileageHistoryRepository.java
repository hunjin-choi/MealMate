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
    @Query(value = "select * from mileage_history as mh left join member member as m where m.name = :memberName order by mh.created_at desc limit 1", nativeQuery = true)
    public Optional<MileageHistory> findLatestBy(String memberName);
    @Query("select new service.chat.mealmate.member.dto.MileageDto(mh) from MileageHistory mh where mh.member = :member order by mh.createdAt ASC")
    public List<MileageDto> dynamicTest(Member member);

    public List<MileageHistory> findByMemberOrderByCreatedAtAsc(Member member);

}
