package kr.co.solpick.auth.security;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.solpick.auth.security.JWTUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.SignatureException;
import java.util.List;
import java.util.Map;
import java.util.Collections;
@Log4j2
public class TokenCheckFilter extends OncePerRequestFilter {
    private JWTUtil jwtUtil;
    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/refrigerator/ingredients",
            "/api/refrigerator/recommend/",
            "/api/user-allergy/",
            "/api/meal-plan"
    );
    public TokenCheckFilter (JWTUtil jwUtil) {
        this.jwtUtil = jwUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!path.startsWith("/api") && !path.startsWith("/solpick/api")) { // /api 주소가 아니면(일반접속이면) 통과
            filterChain.doFilter(request, response);
            return;
        }
        if (path.equals("/solpick/api/points") || path.equals("/solpick/api/payment/verify-card") ||
        path.equals("/solpick/api/points/update")) {
            filterChain.doFilter(request, response);
            return;
        }


        log.info("Token Check Filter............");
        log.info("JWUtil: "+jwtUtil.toString());

        // AccessToken 검증
//        try {
//            validateAccessToken(request);
//            filterChain.doFilter(request, response);
//        } catch (AccessTokenException e) {
//            e.sendResponseError(response);
//        }

        try {
            // 토큰 검증 및 claims 가져오기
            Map<String, Object> claims = validateAccessToken(request);

            // SecurityContext에 인증 정보 설정
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(claims, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (AccessTokenException e) {
            e.sendResponseError(response);
        }

    }

    // AccessToken 검증
    private Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String headerStr = request.getHeader("Authorization");
        if (headerStr == null || headerStr.length() < 8) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }
        // Bearer 생략
        String tokenType = headerStr.substring(0,6);
        String tokenStr = headerStr.substring(7);

        if (tokenType.equalsIgnoreCase("Bearer") == false) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            Map<String, Object> value = jwtUtil.validateToken(tokenStr);
            return value;
        } catch (MalformedJwtException e) {
            log.info("MalformedJwtException................");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch (SignatureException e) {
            log.info("SignaturedException..................");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch(ExpiredJwtException e) {
            log.info("ExpiredJwtException..................");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        } catch (Exception e) {
            log.info("Exception......................");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }
    }

}