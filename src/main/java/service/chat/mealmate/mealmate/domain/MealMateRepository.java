package service.chat.mealmate.mealmate.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import websocket.spring_websocket.member.domain.Member;

import java.util.Optional;

@Repository
public interface MealMateRepository extends JpaRepository<MealMate, Long> {
    public Optional<MealMate> findByOwnerAndIsActive(Member member, Boolean isActive);
}
