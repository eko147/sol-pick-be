package kr.co.solpick.member.service;

import kr.co.solpick.member.dto.MemberDTO;
import kr.co.solpick.member.entity.Member;
import kr.co.solpick.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 이메일로 회원 존재 여부 조회
     */
    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    /**
     * 이메일로 회원 정보 조회
     */
    @Transactional(readOnly = true)
    public MemberDTO getMemberByEmail(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다: " + email));

        return convertToDTO(member);
    }

    /**
     * 새 회원 생성
     */
    @Transactional
    public MemberDTO createMember(MemberDTO memberDTO) {
        // 이메일 중복 체크
        if (memberRepository.existsByEmail(memberDTO.getEmail())) {
            throw new RuntimeException("이미 등록된 이메일입니다: " + memberDTO.getEmail());
        }

        Member member = convertToEntity(memberDTO);
        Member savedMember = memberRepository.save(member);

        log.info("새 회원이 등록되었습니다: {}", savedMember.getEmail());
        return convertToDTO(savedMember);
    }

    /**
     * Entity를 DTO로 변환
     */
    private MemberDTO convertToDTO(Member member) {
        return MemberDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .nickname(member.getNickname())
                .recipickUserId(member.getRecipickUserId())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }

    /**
     * DTO를 Entity로 변환
     */
    private Member convertToEntity(MemberDTO dto) {
        return Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .recipickUserId(dto.getRecipickUserId())
                .phone("")  // 문자열로 직접 값 전달
                .profileImageUrl(null)  // null도 직접 전달
                .active("Y")
                .build();
    }
}