package kr.co.solpick.point.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "point")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "card_id")
    private Integer cardId;

    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "point_amount", nullable = false)
    private Integer pointAmount;

    @Column(name = "point_balance", nullable = false)
    private Integer pointBalance;

    @Column(name = "point_type")
    private String pointType;

    @Column(name = "transaction_amount")
    private Integer transactionAmount;

    @Column(name = "description")
    private String description;

//    @Column(name = "created_date")
//    private LocalDateTime createdDate;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}