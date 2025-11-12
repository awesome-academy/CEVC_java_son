package com.example.expense.entity;

import com.example.expense.enums.CategoryType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 36)
  @Column(nullable = false, unique = true, length = 36)
  private String uuid;

  @NotBlank
  @Size(max = 100)
  private String name;

  @Size(max = 255)
  private String description;

  @Size(max = 100)
  private String icon;

  @Enumerated(EnumType.STRING)
  @Column(length = 20)
  private CategoryType type;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by")
  @JsonBackReference
  private User createdBy;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "updated_by")
  @JsonBackReference
  private User updatedBy;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
