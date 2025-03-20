package kr.co.solpick.external.recipick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecipickLikeResponseDTO {
    @JsonProperty("recipe_id")
    private int recipeId;

    @JsonProperty("recipe_name")
    private String recipeName;

    @JsonProperty("thumbnail_url")
    private String thumbnailUrl;
}
