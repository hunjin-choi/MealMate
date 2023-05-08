package service.chat.mealmate.member.domain;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findFirstByNameOrderByDateDesc(String name);

//    @EntityGraph(attributePaths = {"mealMateList"})
    @Query("SELECT m FROM Member m LEFT JOIN fetch m.mealMateList mm WHERE m.memberId = ?1 and mm.isActive = true")
    Optional<Member> findByIdWithActiveMM(Long memberId);
}
