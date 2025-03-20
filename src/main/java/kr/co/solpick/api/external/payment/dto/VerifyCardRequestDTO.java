package kr.co.solpick.api.external.payment.dto;

import lombok.Data;

@Data
public class VerifyCardRequestDTO {
    private String apiKey;
    private int recipickUserId;
    private String cardNumber;
    private String cardExpiry;
}
