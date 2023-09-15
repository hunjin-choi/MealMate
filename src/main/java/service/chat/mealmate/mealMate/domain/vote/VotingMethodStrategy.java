package service.chat.mealmate.mealMate.domain.vote;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VotingMethodStrategy {
    // hashCode 정의?
//    private Map<String, VotingMethod> votingMethodMap = new HashMap<>();
    private Map<VoteMethodType, VotingMethod> votingMethodMap = new HashMap<>();

    public VotingMethodStrategy() {
        votingMethodMap.put(VoteMethodType.MAJORITY, new MajorVotingMethod());
        votingMethodMap.put(VoteMethodType.UNANIMOUS, new UnanimousVotingMethod());
        votingMethodMap.put(VoteMethodType.NONE, new NonVotingMethod());
        for (VoteMethodType voteMethodType : VoteMethodType.values()) {
            if (votingMethodMap.containsKey(voteMethodType) == false) {
                throw new RuntimeException("일부 strategy 설정이 빠졌습니다: " + voteMethodType.name());
            }
        }
    }

    public boolean executable(VoteMethodType voteMethodType, Long totalMember, Long agreeCount, Long disagreeCount) {
        return this.votingMethodMap.get(voteMethodType).executable(totalMember, agreeCount, disagreeCount);
    }
}
