package com.example.expense.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "budget_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BudgetTemplate {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 36)
  @Column(nullable = false, unique = true, length = 36)
  private String uuid;

  @Size(max = 100)
  @Column(length = 100)
  private String name;

  @Column(name = "period")
  private LocalDate period;

  @NotNull
  @Column(name = "default_amount", precision = 15, scale = 2)
  private BigDecimal defaultAmount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  @JsonBackReference
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "created_by",
      foreignKey = @ForeignKey(name = "fk_budget_templates_created_by"))
  @JsonBackReference
  private User createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "updated_by",
      foreignKey = @ForeignKey(name = "fk_budget_templates_updated_by"))
  @JsonBackReference
  private User updatedBy;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
