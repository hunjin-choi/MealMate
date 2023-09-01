package service.chat.mealmate.mealmate.domain.vote;

import service.chat.mealmate.mealmate.domain.vote.VotingMethod;

public class UnanimousVotingMethod implements VotingMethod {
    @Override
    public boolean executable(Long totalMember, Long agreeCount, Long disagreeCount) {
        return false;
    }
}
