package service.chat.mealmate.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findFirstByName(String name);
    Optional<Member> findByEmail(String email);

    Optional<Member> findByName(String name);
//    @EntityGraph(attributePaths = {"mealMateList"})


}

