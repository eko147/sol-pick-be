package kr.co.solpick.refrigerator.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecipickOrderItemDTO {
    private int userId;
    private int ohId;
    private String name;
    private String emoji;
    private String image;
    private int quantity;
    private String expiryDate;  // ISO 형식 문자열: yyyy-MM-dd'T'HH:mm:ss
    private String mainCategory;
    private String subCategory;
    private String detailCategory;
}