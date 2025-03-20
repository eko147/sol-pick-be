package kr.co.solpick.refrigerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_allergies")
@Getter
@Setter
@NoArgsConstructor
public class UserAllergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Integer userId; // ✅ User 엔티티 없이 user_id를 직접 저장

    @Column(nullable = false)
    private String ingredientName;
}
