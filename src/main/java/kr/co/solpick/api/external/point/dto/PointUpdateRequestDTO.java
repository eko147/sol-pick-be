package kr.co.solpick.api.external.point.dto;

import lombok.Data;

@Data
public class PointUpdateRequestDTO {
    private String apiKey;
    private int memberId;         // recipick user ID
    private int orderId;          // order ID from recipick
    private int pointsUsed;       // points amount to use
    private int totalPrice;       // total price of the order
}