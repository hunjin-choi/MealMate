package service.chat.mealmate.mealMate.domain.vote;

public class NonVotingMethod implements VotingMethod {
    @Override
    public boolean executable(Long totalMember, Long agreeCount, Long disagreeCount) {
        return true;
    }
}
