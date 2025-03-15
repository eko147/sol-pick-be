package kr.co.solpick.point.service;

import kr.co.solpick.member.entity.Member;
import kr.co.solpick.member.repository.MemberRepository;
import kr.co.solpick.point.entity.Point;
import kr.co.solpick.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    private final MemberRepository memberRepository;

    /**
     * 솔픽 회원 ID로 포인트 조회 <- 이건 추 후 수정
     */
    @Transactional(readOnly = true)
    public int getUserPoints(int userId) {
        log.info("솔픽 회원 ID로 포인트 조회: userId={}", userId);
        return pointRepository.findLatestPointBalanceByUserId(userId)
                .orElse(0);
    }

    /**
     * 레시픽 회원 ID로 포인트 조회
     */
    @Transactional(readOnly = true)
    public int getUserPointsByRecipickUserId(int recipickUserId) {
        log.info("레시픽 회원 ID로 포인트 조회: recipickUserId={}", recipickUserId);

        return pointRepository.findLatestPointBalanceByRecipickUserId(recipickUserId)
                .orElseGet(() -> {
                    log.warn("레시픽 회원 ID에 해당하는 포인트 정보가 없습니다: recipickUserId={}", recipickUserId);
                    return 0;
                });
    }
}