package service.chat.mealmate.mealmate.domain;

public interface VotingMethod {
    public boolean executable(Long totalMember, Long agreeCount, Long disagreeCount);
}
