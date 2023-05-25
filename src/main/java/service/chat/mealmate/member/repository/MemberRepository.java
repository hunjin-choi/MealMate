package service.chat.mealmate.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.member.domain.Member;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findFirstByName(String name);
    Optional<Member> findByEmail(String email);

//    @EntityGraph(attributePaths = {"mealMateList"})
}

