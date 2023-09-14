package service.chat.mealmate.mealMate.domain.vote;

public interface VotingMethod {
    public boolean executable(Long totalMember, Long agreeCount, Long disagreeCount);
}
