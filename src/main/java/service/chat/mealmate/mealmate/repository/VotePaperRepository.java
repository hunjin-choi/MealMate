package service.chat.mealmate.mealmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.vote.VotePaper;

@Repository
public interface VotePaperRepository extends JpaRepository<VotePaper, Long> {
}
