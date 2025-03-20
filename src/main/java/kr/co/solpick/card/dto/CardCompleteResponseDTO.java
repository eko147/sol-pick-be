package kr.co.solpick.card.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CardCompleteResponseDTO {
    private Integer cardId;
    private String cardNumber;
    private String cvcNumber;
    private String expiryDate;
    private String lastName;
    private String firstName;
    private Integer backgroundId;
    private String stickersData;
}
