package service.chat.mealmate.mealmate.domain.vote;

public interface VotingMethod {
    public boolean executable(Long totalMember, Long agreeCount, Long disagreeCount);
}
