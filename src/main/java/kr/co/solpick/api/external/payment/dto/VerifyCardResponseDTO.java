package kr.co.solpick.api.external.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyCardResponseDTO {
    private boolean success;
    private String message;
    private boolean isValid;
}