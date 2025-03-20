package kr.co.solpick.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDTO {
    private Integer cardId;
    private Integer userId;
    private String cardNumber;
    private LocalDate issueDate;
    private String expiredAt;
    private String lastName;
    private String firstName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}