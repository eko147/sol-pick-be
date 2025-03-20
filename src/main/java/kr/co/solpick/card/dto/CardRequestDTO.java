package kr.co.solpick.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRequestDTO {
    private Integer userId;
    private String lastName;    // 영문 성
    private String firstName;   // 영문 이름
}