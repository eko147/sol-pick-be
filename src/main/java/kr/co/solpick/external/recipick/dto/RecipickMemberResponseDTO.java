package kr.co.solpick.external.recipick.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RecipickMemberResponseDTO {
    @JsonProperty("member_id")
    private int memberId;
    private String email;
    private String name;
    private String nickname;
    private String gender;
    private String birth;
}