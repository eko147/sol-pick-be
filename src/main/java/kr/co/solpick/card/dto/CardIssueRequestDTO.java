package kr.co.solpick.card.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardIssueRequestDTO {
    private Integer userId;
    private Integer designId;
    private String lastName;
    private String firstName;
}
