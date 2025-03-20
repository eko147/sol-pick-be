package kr.co.solpick.external.recipick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecipickStepRequestDTO {
    @JsonProperty("recipe_id")
    private int recipeId;
    private String apiKey;
}