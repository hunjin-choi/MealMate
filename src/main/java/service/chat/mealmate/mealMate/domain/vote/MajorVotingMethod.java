package service.chat.mealmate.mealMate.domain.vote;

public class MajorVotingMethod implements VotingMethod {
    @Override
    public boolean executable(Long totalMember, Long agreeCount, Long disagreeCount) {
        if (agreeCount / totalMember >= 0.5) return true;
        else return false;
    }
}
