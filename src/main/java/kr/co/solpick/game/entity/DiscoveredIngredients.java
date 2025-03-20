package kr.co.solpick.game.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "game_discovered_ingredients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DiscoveredIngredients {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "recipe_id", nullable = false)
    private Integer recipeId;

    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @Column(name = "discovered", nullable = false)
    private Boolean discovered = false;

    @Column(name = "count", nullable = false)
    private Integer count = 0;

    @Column(name = "required_quantity", nullable = false)
    private Integer requiredQuantity;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}