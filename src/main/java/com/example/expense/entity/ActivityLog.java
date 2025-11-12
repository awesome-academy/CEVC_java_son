package com.example.expense.entity;

import com.example.expense.enums.Action;
import com.example.expense.enums.EntityType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ActivityLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 36)
  @Column(nullable = false, unique = true, length = 36)
  private String uuid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_activity_logs_user_id"))
  @JsonBackReference
  private User user;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private Action action;

  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(name = "entity_type", length = 20, nullable = false)
  private EntityType entityType;

  @NotNull
  @Column(name = "entity_id", nullable = false)
  private Long entityId;

  @Lob private String description;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
