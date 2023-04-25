package service.chat.mealmate.member.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@SpringBootTest @Transactional
class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @PersistenceContext
    private EntityManager em;

    @Test @DisplayName("유저를 생성합니다")
    public void signUpUser() {
        Long pastUserCount = (Long) em.createQuery("select count(m) from Member m").getSingleResult();
        em.createQuery("select count(mh) from MileageHistory  mh").getSingleResult();
        memberService.signUp("tempUser");
        Long currentUserCount = (Long) em.createQuery("select count(m) from Member m").getSingleResult();
        em.createQuery("select count(mh) from MileageHistory  mh").getResultList();
    }

    @Test @DisplayName("유저를 생성하면 그 유저의 mileageHistory도 생성되어야 합니다.")
    public void IfUserSignUpThenCreateMileageHistory() {

    }
}