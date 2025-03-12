package kr.co.solpick.auth.service;

import kr.co.solpick.auth.dto.AuthRequestDTO;
import kr.co.solpick.auth.dto.AuthResponseDTO;
//import kr.co.solpick.auth.security.JwtTokenProvider;
import kr.co.solpick.auth.security.JWTUtil;
import kr.co.solpick.external.recipick.client.RecipickAuthClient;
import kr.co.solpick.external.recipick.dto.RecipickMemberResponseDTO;
import kr.co.solpick.member.dto.MemberDTO;
import kr.co.solpick.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final RecipickAuthClient recipickAuthClient;
    private final MemberService memberService;
    private final JWTUtil jwtUtil;
//    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponseDTO login(AuthRequestDTO loginRequest) {
        log.info("로그인 요청 처리: {}", loginRequest.getEmail());

        // 1. Recipick API를 통해 인증 진행
        boolean isAuthenticated = recipickAuthClient.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (!isAuthenticated) {
            log.error("인증 실패: {}", loginRequest.getEmail());
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // 2. 이메일로 솔픽 회원 여부 확인
        boolean memberExists = memberService.existsByEmail(loginRequest.getEmail());

        MemberDTO memberInfo;

        if (!memberExists) {
            // 3. 첫 로그인인 경우 레시픽에서 사용자 정보 가져오기
            RecipickMemberResponseDTO recipickUser = recipickAuthClient.getUserInfo(loginRequest.getEmail());


            // 4. 솔픽 DB에 사용자 정보 저장
            MemberDTO newMember = MemberDTO.builder()
                    .email(recipickUser.getEmail())
                    .name(recipickUser.getName())
                    .nickname(recipickUser.getNickname())
                    .recipickUserId(recipickUser.getMemberId())
                    .build();

            memberInfo = memberService.createMember(newMember);
            log.info("새 회원 등록 완료: {}", memberInfo.getEmail());
        } else {
            // 5. 기존 회원은 솔픽 DB에서 정보 가져오기
            memberInfo = memberService.getMemberByEmail(loginRequest.getEmail());
            log.info("기존 회원 로그인: {}", memberInfo.getEmail());
            log.info("레시픽 사용자 정보: memberId={}", memberInfo.getRecipickUserId());
        }

        // 6. JWT 토큰 생성
//        String token = jwtTokenProvider.createToken(memberInfo.getEmail(), memberInfo.getId());

//        String dummyToken = "dummy-token-" + memberInfo.getId();


        String token = jwtUtil.generationToken(
                Map.of(
                        "email", memberInfo.getEmail(),
                        "id", memberInfo.getId(),
                        "recipickUserId", memberInfo.getRecipickUserId()
                ), 1);

        // 7. 로그인 응답 생성
        return AuthResponseDTO.builder()
                .token(token)
//                .token(dummyToken)
                .memberId(memberInfo.getId())
                .recipickUserId(memberInfo.getRecipickUserId())
                .email(memberInfo.getEmail())
                .name(memberInfo.getName())
                .nickname(memberInfo.getNickname())
                .build();
    }
}