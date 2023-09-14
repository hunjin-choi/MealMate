package service.chat.mealmate.mealMate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealMate.domain.vote.VotePaper;

@Repository
public interface VotePaperRepository extends JpaRepository<VotePaper, Long> {
}
