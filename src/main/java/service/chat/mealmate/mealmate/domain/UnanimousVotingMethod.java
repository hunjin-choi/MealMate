package service.chat.mealmate.mealmate.domain;

public class UnanimousVotingMethod implements VotingMethod{
    @Override
    public boolean executable(Long totalMember, Long agreeCount, Long disagreeCount) {
        return false;
    }
}
