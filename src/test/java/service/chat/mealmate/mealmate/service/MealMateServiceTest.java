package service.chat.mealmate.mealmate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.mealmate.domain.ChatMessage;
import service.chat.mealmate.mealmate.domain.FeedbackHistory;
import service.chat.mealmate.mealmate.repository.FeedbackHistoryRepository;
import service.chat.mealmate.mealmate.domain.MealMate;
import service.chat.mealmate.mealmate.repository.MealMateRepository;
import service.chat.mealmate.member.domain.Member;
import service.chat.mealmate.member.repository.MemberRepository;
import service.chat.mealmate.member.service.MemberService;
import service.chat.mealmate.mileageHistory.domain.MileageChangeReason;
import service.chat.mealmate.mileageHistory.domain.MileageHistory;
import service.chat.mealmate.mileageHistory.repository.MileageHistoryRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@SpringBootTest @Transactional
class MealMateServiceTest {
    @Autowired
    private MealmateService mealMateService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MealMateRepository mealMateRepository;
    @Autowired
    private FeedbackHistoryRepository feedbackHistoryRepository;
    @Autowired
    private MileageHistoryRepository mileageHistoryRepository;
    @PersistenceContext
    private EntityManager em;

    Member lhs_m1; Member rhs_m1; Member lhs_m2; Member rhs_m2;

    @BeforeEach
    public void beforeEach() {
        memberService.signUp("lhs_m1"); memberService.signUp("rhs_m1");
        memberService.signUp("lhs_m2"); memberService.signUp("rhs_m2");
        lhs_m1 = em.createQuery("select m from Member m where m.name=?1", Member.class).setParameter(1, "lhs_m1").getSingleResult();
        rhs_m1 = em.createQuery("select m from Member m where m.name=?1", Member.class).setParameter(1, "rhs_m1").getSingleResult();
        lhs_m2 = em.createQuery("select m from Member m where m.name=?1", Member.class).setParameter(1, "lhs_m2").getSingleResult();
        rhs_m2 = em.createQuery("select m from Member m where m.name=?1", Member.class).setParameter(1, "rhs_m2").getSingleResult();
    }

    @Test @DisplayName("채팅메시지 저장 로직이 잘 동작하는지 확인합니다")
    @Rollback(value = false)
    public void addChatMessage() throws Exception {
        // given
        String chatRoomId = "testChatRoomId";
        String chatMessage = "this is test";
        // when
        mealMateService.connectMealMate(lhs_m1.getMemberId(), rhs_m1.getMemberId(), chatRoomId);
        mealMateService.saveChatMessage(chatMessage, chatRoomId, lhs_m1.getMemberId());
        // then
        List<ChatMessage> content = mealMateService.findChatMessagePageable(chatRoomId, lhs_m1.getMemberId(), 0, 1);
        Assertions.assertEquals(1, content.size());
        Assertions.assertEquals(chatMessage, content.get(0).getMessage());
    }

//    @Test @DisplayName("두 사용자를 매칭합니다.")
//    public void connectTwoUsers() {
//        mealMateService.connectMealMate(lhs_m1.getMemberId(), rhs_m1.getMemberId());
//        MealMate mealMate = em.createQuery("select mm from MealMate mm where mm.isActive = ?1 and mm.giver = ?2", MealMate.class).setParameter(1, true).setParameter(2, lhs_m1).getSingleResult();
//        Assertions.assertNotNull(mealMate);
//        mealMate = em.createQuery("select mm from MealMate mm where mm.isActive = ?1 and mm.receiver = ?2", MealMate.class).setParameter(1, true).setParameter(2, lhs_m1).getSingleResult();
//        Assertions.assertNotNull(mealMate);
//    }
//
//    @Test @DisplayName("feedbackHistory에 대해 헷갈릴 수 있는 부분")
//    public void clarifyFeedbackHistory() throws Exception {
//        // given
//        Member giver = lhs_m1; Member receiver = rhs_m1;
//        mealMateService.connectMealMate(giver.getMemberId(), receiver.getMemberId());
//        // when
//        mealMateService.confirm(giver.getMemberId(), "goodJob!");
//        MealMate mealMate = mealMateRepository.findByGiverAndIsActive(giver, true).orElseThrow(() -> new RuntimeException(""));
//        FeedbackHistory feedbackHistory = feedbackHistoryRepository.findFirstByMealMateOrderByFeedBackDateDesc(mealMate);
//        MileageHistory giverMileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(giver);
//        MileageHistory receiverMileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(receiver);
//        // then
//        Assertions.assertEquals(feedbackHistory.getFeedbackMention(), "goodJob!");
//        Assertions.assertEquals(feedbackHistory.getFeedbackMileage(), 10L);
//        Assertions.assertEquals(feedbackHistory.getMealMate().getGiver(), giver);
//        Assertions.assertEquals(feedbackHistory.getMealMate().getReceiver(), receiver);
//
//        Assertions.assertEquals(giverMileageHistory.getMileage().getCurrentMileage(), 0L);
//        Assertions.assertEquals(receiverMileageHistory.getMileage().getCurrentMileage(), 10L);
//    }
//    @Test @DisplayName("mileageHistory 객체 동일성을 체크합니다 (receiver로 부터 mileageHistory 조회 가능하고 feedbackHistory로 부터 mileageHistory 조회 가능해야 합니다)(정합성)")
//    public void checkMileageHistoryEquals() throws Exception {
//        // given
//        Member giver = lhs_m1; Member receiver = rhs_m1;
//        mealMateService.connectMealMate(giver.getMemberId(), receiver.getMemberId());
//        // when
//        MealMate mealMate = mealMateRepository.findByGiverAndIsActive(giver, true).orElseThrow(() -> new RuntimeException(""));
//        mealMateService.confirm(giver.getMemberId(), "goodJob!");
//        FeedbackHistory feedbackHistory = feedbackHistoryRepository.findFirstByMealMateOrderByFeedBackDateDesc(mealMate);
//        MileageHistory receiverMileageHistory_1 = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(receiver);
////        MileageHistory receiverMileageHistory_2 = mileageHistoryRepository.findFirstByFeedBackHistoryIdOrOrderByDateDesc(feedbackHistory.getFeedBackHistoryId()).orElseThrow(() -> new RuntimeException());
//        // then
//        Assertions.assertEquals(receiverMileageHistory_1, receiverMileageHistory_2);
//    }
//    @Test @DisplayName("member가 mealmate 상대방을 confirm 하면 상대방 마일리지가 증가해야 합니다.")
//    public void confirmToMealMate() {
//        // given
//        Member giver = lhs_m1; Member receiver = rhs_m1;
//        mealMateService.connectMealMate(giver.getMemberId(), receiver.getMemberId());
//        // when
//        mealMateService.confirm(giver.getMemberId(), "goodJob!");
//        MealMate mealMate = em.createQuery("select mm from MealMate mm where mm.isActive = ?1 and mm.giver = ?2", MealMate.class).setParameter(1, true).setParameter(2, giver).getSingleResult();
//        FeedbackHistory feedbackHistory = feedbackHistoryRepository.findFirstByMealMateOrderByFeedBackDateDesc(mealMate);
//        MileageHistory giverMileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(giver);
//        MileageHistory receiverMileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(receiver);
//        // then
//        Assertions.assertEquals(mealMate.getMealMateMileage(), 10);
//
//        Assertions.assertNotNull(feedbackHistory);
//
//        Assertions.assertEquals(giverMileageHistory.getMileage().getCurrentMileage(), 0L);
//        Assertions.assertEquals(giverMileageHistory.getMileageChangeReason(), MileageChangeReason.INIT);
//        Assertions.assertEquals(receiverMileageHistory.getMileage().getCurrentMileage(), 10L);
//        Assertions.assertEquals(receiverMileageHistory.getMileageChangeReason(), MileageChangeReason.FEEDBACK);
//    }
//
//    @Test @DisplayName("confirm을 여러번 합니다. 적절한 값으로 세팅되는지 확인합니다.")
//    public void multipleConfirm() throws Exception {
//        // given
//        Member giver = lhs_m1; Member receiver = rhs_m1;
//        mealMateService.connectMealMate(giver.getMemberId(), receiver.getMemberId());
//        Member member = memberRepository.findByIdWithActiveMM(giver.getMemberId()).orElseThrow(() -> new RuntimeException());
//        Assertions.assertNotNull(member.getMealMateList());
//        // when
//        mealMateService.confirm(giver.getMemberId(), "firstConfirm!");
//        mealMateService.confirm(giver.getMemberId(), "secondConfirm!");
//        mealMateService.confirm(giver.getMemberId(), "thirdConfirm!");
//        // then
//        MealMate mealMate = mealMateRepository.findByGiverAndIsActive(giver, true).orElseThrow(() -> new RuntimeException());
//        Assertions.assertEquals(mealMate.getMealMateMileage(), 30);
//        Long feedbackCount = feedbackHistoryRepository.countAllByMealMate(mealMate);
//        Assertions.assertEquals(feedbackCount, 3);
//        Long mileageHistoryCount = mileageHistoryRepository.countAllByMember(receiver);
//        Assertions.assertEquals(1L + 3L, mileageHistoryCount);
//        MileageHistory mileageHistory = mileageHistoryRepository.findFirstByMemberOrderByDateDesc(receiver);
//        Assertions.assertEquals(mileageHistory.getMileage().getCurrentMileage(), 10L * 3);
//    }
//    @Testable @DisplayName("member가 마일리지로 하나의 상품을 구매하면, 해당 상품의 가격만큼 마일리지가 차감됩니다.")
//    public void memberBuyProductThenMileageDecreaseProperly() throws Exception {
//        // given
//
//        // when
//
//        // then
//
//    }
//
//    @Test @DisplayName("한 멤버가 순차적으로 여러 밀메이트와 매칭됩니다")
//    public void oneMemberMultipleMatching() throws Exception {
//        // given
//        mealMateService.connectMealMate(lhs_m1.getMemberId(), rhs_m1.getMemberId());
//        mealMateService.disConnectMealMate(lhs_m1.getMemberId(), rhs_m1.getMemberId());
//        mealMateService.connectMealMate(lhs_m1.getMemberId(), lhs_m2.getMemberId());
//        mealMateService.disConnectMealMate(lhs_m1.getMemberId(), lhs_m2.getMemberId());
//        mealMateService.connectMealMate(lhs_m1.getMemberId(), rhs_m2.getMemberId());
//        // when
//        List resultList = em.createQuery("SELECT m FROM Member m LEFT JOIN m.mealMateList mm WHERE m.memberId = ?1 AND mm.isActive = true").setParameter(1, lhs_m1.getMemberId()).getResultList();
//        Member member = memberRepository.findByIdWithActiveMM(lhs_m1.getMemberId()).orElseThrow(() -> new RuntimeException(""));
//        List<MealMate> activeMealMateList = member.getMealMateList();
////        List<MealMate> allMealMateList = em.createQuery("select mm from MealMate mm order by mm.connectDate", MealMate.class).getResultList();
//        // then
////        Assertions.assertEquals(allMealMateList.size(), 3 * 2);
//        Assertions.assertEquals(activeMealMateList.size(), 1);
//        MealMate activeMealMate = activeMealMateList.get(0);
//        Assertions.assertEquals(activeMealMate.getReceiver(), lhs_m1);
//    }
//
//    @Testable @DisplayName("자기 자신과 밀메이트로 매칭될 수는 없습니다.")
//    public void selfMatchIllegar() throws Exception {
//        // given
//
//        // when
//
//        // then
//
//    }
}