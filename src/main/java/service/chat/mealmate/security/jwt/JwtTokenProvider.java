package service.chat.mealmate.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import service.chat.mealmate.security.domain.AppUserRole;
import service.chat.mealmate.utils.DateUtil;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private String readOnly = "ReadOnly";
    private String readWrite = "ReadWrite";
    private String chatRoomId = "ChatRoomId";
    private String chatPeriodId = "ChatPeriodId";

    static public String CHAT_TOKEN = "chatJWT";
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }


    /**
     * 이름으로 Jwt Token을 생성한다.
     */
    public String generateAccessToken(String name, List<AppUserRole> appUserRoles, Date expiredDate) {
        Claims claims = Jwts.claims().setSubject(name);
        claims.put("auth", appUserRoles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).
                filter(Objects::nonNull).
                collect(Collectors.toList()));
        return Jwts.builder()
                .setId(name)
                .setClaims(claims)
                .setIssuedAt(DateUtil.getNow()) // 토큰 발행일자
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public String generateChatAccessToken(String name, String chatRoomId, Long chatPeriodId, Date expiredDate) {
        if (expiredDate == null) return null;
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(name);
//        claims.put("auth", appUserRoles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).
//                filter(Objects::nonNull).
//                collect(Collectors.toList()));
        claims.put(readOnly, false);
        claims.put(readWrite, true);
        claims.put(this.chatRoomId, chatRoomId);
        claims.put(this.chatPeriodId, chatPeriodId);
        return Jwts.builder()
                .setId(name)
                .setClaims(claims)
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public String generateAccessTokenFromRefreshToken(String refreshTokenFromDB, Date expiredDate) {
        if (expiredDate == null) return null;
        Claims claims = getClaims(refreshTokenFromDB).getBody();
        String name = claims.getSubject();
        return Jwts.builder()
                .setId(name)
                .setClaims(claims)
                .setIssuedAt(DateUtil.getNow()) // 토큰 발행일자
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public String generateChatRefreshToken(String name, String chatRoomId, Long chatPeriodId, Date expiredDate) {
        if (expiredDate == null) return null;
        Date now = new Date();
        Claims claims = Jwts.claims().setSubject(name);
//        claims.put("auth", appUserRoles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).
//                filter(Objects::nonNull).
//                collect(Collectors.toList()));
        claims.put(readOnly, false);
        claims.put(readWrite, true);
        claims.put(this.chatRoomId, chatRoomId);
        claims.put(this.chatPeriodId, chatPeriodId);
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

    public String generateReadWriteToken(String name, String chatRoomId, Long chatPeriodId, List<AppUserRole> appUserRoles, Date expiredDate) {
        Date now = new Date();
        // claim에 put, claim.subject에 put 두 방식의 차이는? 보통 중요한 하나의 시그니쳐 값을 subject에 넣나보다
        Claims claims = Jwts.claims().setSubject(name);
        claims.put("auth", appUserRoles.stream().map(s -> new SimpleGrantedAuthority(s.getAuthority())).
                filter(Objects::nonNull).
                collect(Collectors.toList()));
        claims.put(readOnly, true);
        claims.put(readWrite, true);
        claims.put(this.chatRoomId, chatRoomId);
        claims.put(this.chatPeriodId, chatPeriodId);
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
    public Optional<String> getUserNameFromJwt(String jwt) {
//        return getClaims(jwt).getBody().getId(); <- 이거 하면 return 값이 null
        try{
            return Optional.ofNullable(getClaims(jwt).getBody().getSubject());
        }catch (Exception e) {
            return null;
        }
    }
    public Optional<String> getChatRoomIdFromJWT(String jwt) {
        try{
            return Optional.ofNullable((String) getClaims(jwt).getBody().get(chatRoomId));
        }catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Long> getChatPeriodIdFromJWT(String jwt) {
        try{
            return Optional.ofNullable( (Long)getClaims(jwt).getBody().get(chatPeriodId));
        }catch (Exception e) {
            return Optional.empty();
        }
    }
    /**
     * Jwt Token의 유효성을 체크한다.
     */
    public boolean validateToken(String jwt) {
        try {
            return this.getClaims(jwt) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public void validateReadOnlyToken(String jwt) {
        Jws<Claims> claims = this.getClaims(jwt);
        boolean claimCheck = (claims != null);
        if (claimCheck == false) throw new RuntimeException("");
        if (isReadOnlyJWT(jwt)) return ;
        else throw new RuntimeException("");
    }

    public void validateReadWriteToken(String jwt) {
        Jws<Claims> claims = this.getClaims(jwt);
        boolean claimCheck = (claims != null);
        if (claimCheck == false) throw new RuntimeException("");
        if (isReadWriteJWT(jwt)) return ;
        else throw new RuntimeException("");
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
