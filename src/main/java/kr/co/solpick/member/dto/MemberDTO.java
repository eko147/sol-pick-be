package kr.co.solpick.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {
    private Integer id;
    private String email;
    private String name;
    private String nickname;
    private Integer recipickUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;    // 요청/응답 공통
}