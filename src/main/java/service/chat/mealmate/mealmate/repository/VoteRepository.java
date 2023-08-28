package service.chat.mealmate.mealmate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.chat.mealmate.mealmate.domain.Vote;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Integer>{
}
