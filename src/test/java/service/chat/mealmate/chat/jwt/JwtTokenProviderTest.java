package service.chat.mealmate.chat.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import service.chat.mealmate.chat.config.AppUserRole;
import service.chat.mealmate.utils.DateUtil;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JwtTokenProviderTest {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test @DisplayName("토큰 생성시 값이 payload 값이 잘 설정되는지 확인합니다")
    public void checkJWTPayload() throws Exception {
        // given
        ArrayList<AppUserRole> roles = new ArrayList<>();
        roles.add(AppUserRole.ROLE_CLIENT); roles.add(AppUserRole.ROLE_ADMIN);
        String roomId = "testRoomId";
        // when
        String jwt = jwtTokenProvider.generateAccessToken("huchoi", roomId, roles, DateUtil.addDaysFromNow(10));
        // then
        assertEquals("huchoi", jwtTokenProvider.getUserNameFromJwt(jwt));
        assertEquals(roomId, jwtTokenProvider.getChatRoomIdFromJWT(jwt));
        assertFalse(jwtTokenProvider.isReadOnlyJWT(jwt));
        assertTrue(jwtTokenProvider.isReadWriteJWT(jwt));
    }
}