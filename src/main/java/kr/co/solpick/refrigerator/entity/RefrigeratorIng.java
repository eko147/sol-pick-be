package kr.co.solpick.refrigerator.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "refrigerator_ing") // 테이블명 지정
public class RefrigeratorIng {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refrigerator_ing_id") // 기본키 컬럼명 설정
    private Integer id;

    @Column(name = "user_id", nullable = true)
    private Integer userId;

    @Column(name = "refrigerator_ing_emoji", nullable = true, length = 255)
    private String emoji;

    @Column(name = "refrigerator_ing_name", nullable = true, length = 255)
    private String name;

    @Column(name = "refrigerator_ing_img", nullable = true, length = 255)
    private String img;

    @Column(name = "quantity", nullable = true)
    private Integer quantity;

    @Column(name = "expiry_date", nullable = true)
    private LocalDateTime expiryDate;

    @Column(name = "main_category", nullable = true, length = 255)
    private String mainCategory;

    @Column(name = "sub_category", nullable = true, length = 255)
    private String subCategory;

    @Column(name = "detail_category", nullable = true, length = 255)
    private String detailCategory;

    @Column(name = "created_at", nullable = true)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
}
