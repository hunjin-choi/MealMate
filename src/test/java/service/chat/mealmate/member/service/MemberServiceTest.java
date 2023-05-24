package service.chat.mealmate.member.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.mileageHistory.domain.MileageChangeReason;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;
import service.chat.mealmate.utils.DateUtil;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@SpringBootTest @Transactional
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MileageHistoryRepository mileageHistoryRepository;
    @PersistenceContext
    private EntityManager em;

    @Test @DisplayName("유저를 생성합니다")
    public void signUpUser() {
        // given
        long pastMemberCount = memberRepository.count();
        // when
        memberService.signUp("tempUser");
        long currentMemberCount = memberRepository.count();
        Member member = memberRepository.findFirstByName("tempUser").orElseThrow(() -> new RuntimeException());
        // then
        Assertions.assertEquals(pastMemberCount + 1, currentMemberCount);
        Assertions.assertEquals("tempUser", member.getName());
    }

    @Test @DisplayName("유저를 생성하면 그 유저의 mileageHistory도 생성되어야 합니다.")
    public void ifUserSignUpThenCreateMileageHistory() {
        // given
        long pastMHCount = mileageHistoryRepository.count();
        // when
        memberService.signUp("tempUser");
        Member member = memberRepository.findFirstByName("tempUser").orElseThrow(() -> new RuntimeException());
        long currentMHCount = mileageHistoryRepository.count();
        MileageHistory mileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(member);
        // then
        Assertions.assertEquals(pastMHCount + 1, currentMHCount);
        Assertions.assertEquals(0, mileageHistory.getMileage().getCurrentMileage());
        Assertions.assertEquals(MileageChangeReason.INIT, mileageHistory.getMileageChangeReason());
//        Assertions.assertNull(mileageHistory.getFeedBackHistory());
//        Assertions.assertNull(mileageHistory.getOrders());
        Assertions.assertTrue(DateUtil.isSameDateWithoutTime(new Date(), mileageHistory.getDate()));
        Assertions.assertTrue(member.equals(mileageHistory.getMember()));
    }
}