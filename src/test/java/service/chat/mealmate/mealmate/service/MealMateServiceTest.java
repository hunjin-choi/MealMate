package service.chat.mealmate.mealmate.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import websocket.spring_websocket.mealmate.domain.MealMate;
import websocket.spring_websocket.member.domain.Member;
import websocket.spring_websocket.member.service.MemberService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest @Transactional
class MealMateServiceTest {
    @Autowired
    private MealMateService mealMateService;
    @Autowired
    private MemberService memberService;
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

    @Test @DisplayName("두 사용자를 매칭합니다.")
    public void connectTwoUsers() {
        mealMateService.connectMealMate(lhs_m1.getUserId(), rhs_m1.getUserId());
        MealMate mealMate = em.createQuery("select mm from MealMate mm where mm.isActive = ?1 and mm.owner = ?2", MealMate.class).setParameter(1, true).setParameter(2, lhs_m1).getSingleResult();
        Assertions.assertNotNull(mealMate);
        mealMate = em.createQuery("select mm from MealMate mm where mm.isActive = ?1 and mm.member = ?2", MealMate.class).setParameter(1, true).setParameter(2, lhs_m1).getSingleResult();
        Assertions.assertNotNull(mealMate);
    }

    @Test @DisplayName("user1이 mealmate 상대에게 confirm을 하면 상대 마일리지가 증가해야 합니다.")
    public void confirmToMealMate() {
        // given
        mealMateService.connectMealMate(lhs_m1.getUserId(), rhs_m1.getUserId());
        // when
        mealMateService.confirm(lhs_m1.getUserId(), "goodJob!");
        MealMate mealMate = em.createQuery("select mm from MealMate mm where mm.isActive = ?1 and mm.owner = ?2", MealMate.class).setParameter(1, true).setParameter(2, lhs_m1).getSingleResult();
        // then
        Assertions.assertEquals(mealMate.getMealMateMileage(), 10);
    }
}