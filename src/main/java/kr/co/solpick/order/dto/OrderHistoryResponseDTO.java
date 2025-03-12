package kr.co.solpick.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistoryResponseDTO {
    private String orderDateTime;
    private String itemName;
    private double price;
}