package kr.co.solpick.refrigerator.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refrigerator_ing")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refrigerator_ing_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "refrigerator_ing_img", columnDefinition = "LONGTEXT")
    private String image;

    @Column(name = "refrigerator_ing_name")
    private String name;

    @Column(name = "refrigerator_ing_emoji")
    private String emoji;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "main_category")
    private String mainCategory;

    @Column(name = "sub_category")
    private String subCategory;

    @Column(name = "detail_category")
    private String detailCategory;

    // 수정 메서드
    public void update(String name, String emoji, String image, Integer quantity, LocalDateTime expiryDate,
                       String mainCategory, String subCategory, String detailCategory) {
        this.name = name;
        this.emoji = emoji;
        this.image = image;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.mainCategory = mainCategory;
        this.subCategory = subCategory;
        this.detailCategory = detailCategory;
        this.updatedAt = LocalDateTime.now();
    }
}