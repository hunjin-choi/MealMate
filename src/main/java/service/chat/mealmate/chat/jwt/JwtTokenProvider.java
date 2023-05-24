package service.chat.mealmate.chat.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import service.chat.mealmate.chat.config.AppUserRole;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private String readOnly = "ReadOnly";
    private String readWrite = "ReadWrite";
    private String chatRoomId = "ChatRoomId";
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    /**
     * 이름으로 Jwt Token을 생성한다.
     */
    public String generateReadWriteJWT(String name, String chatRoomId, List<AppUserRole> appUserRoles, Date expiredDate) {
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(name);
        claims.put("auth", appUserRoles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).
                filter(Objects::nonNull).
                collect(Collectors.toList()));
        claims.put(readOnly, false);
        claims.put(readWrite, true);
        claims.put(this.chatRoomId, chatRoomId);
        return Jwts.builder()
                .setId(name)
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public boolean isReadWriteJWT(String jwt) {
        return (boolean) getClaims(jwt).getBody().get(readWrite);
    }
    public String generateReadOnlyJWT(String name, String chatRoomId, List<AppUserRole> appUserRoles, Date expiredDate) {
        Date now = new Date();
        // claim에 put, claim.subject에 put 두 방식의 차이는? 보통 중요한 하나의 시그니쳐 값을 subject에 넣나보다
        Claims claims = Jwts.claims().setSubject(name);
        claims.put("auth", appUserRoles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).
                filter(Objects::nonNull).
                collect(Collectors.toList()));
        claims.put(readOnly, true);
        claims.put(readWrite, false);
        claims.put(this.chatRoomId, chatRoomId);
        return Jwts.builder()
                .setId(name)
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }
    public boolean isReadOnlyJWT(String jwt) {
        return (boolean) getClaims(jwt).getBody().get(readOnly);
    }
    /**
     * Jwt Token을 복호화 하여 이름을 얻는다.
     */
    public String getUserNameFromJwt(String jwt) {
//        return getClaims(jwt).getBody().getId(); <- 이거 하면 return 값이 null
        return getClaims(jwt).getBody().getSubject(); //
    }
    public String getChatRoomIdFromJWT(String jwt) {
        return (String) getClaims(jwt).getBody().get(chatRoomId);
    }
    /**
     * Jwt Token의 유효성을 체크한다.
     */
    public boolean validateToken(String jwt) {
        return this.getClaims(jwt) != null;
    }

    public Jws<Claims> getClaims(String jwt) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw ex;
        }
    }
}
