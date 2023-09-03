package service.chat.mealmate.mealmate.domain.vote;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class VotingMethodStrategy {
    // hashCode 정의?
//    private Map<String, VotingMethod> votingMethodMap = new HashMap<>();
    private Map<VotingMethodType, VotingMethod> votingMethodMap = new HashMap<>();

    public VotingMethodStrategy() {
        votingMethodMap.put(VotingMethodType.MAJORITY, new MajorVotingMethod());
        votingMethodMap.put(VotingMethodType.UNANIMOUS, new UnanimousVotingMethod());
        votingMethodMap.put(VotingMethodType.NONE, new NonVotingMethod());
        for (VotingMethodType votingMethodType : VotingMethodType.values()) {
            if (votingMethodMap.containsKey(votingMethodType) == false) {
                throw new RuntimeException("일부 strategy 설정이 빠졌습니다: " + votingMethodType.name());
            }
        }
    }

    public boolean executable(VotingMethodType votingMethodType, Long totalMember, Long agreeCount, Long disagreeCount) {
        return this.votingMethodMap.get(votingMethodType).executable(totalMember, agreeCount, disagreeCount);
    }
}
