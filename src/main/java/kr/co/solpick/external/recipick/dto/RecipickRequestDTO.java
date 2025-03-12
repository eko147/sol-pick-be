package kr.co.solpick.external.recipick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecipickRequestDTO {
    private String apiKey;
    @JsonProperty("member_id")
    private int memberId;
}
