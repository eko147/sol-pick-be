package kr.co.solpick.external.recipick.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
public class RecipickAuthRequestDTO {
    private String email;
    private String password;
    private String apiKey;
}
