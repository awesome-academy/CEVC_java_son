package com.example.expense.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Attachment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true, length = 36)
  private String uuid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "expense_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_attachments_expense_id"))
  @JsonBackReference
  private Expense expense;

  @NotBlank
  @Size(max = 255)
  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName;

  @NotBlank
  @Size(max = 255)
  @Column(name = "file_path", nullable = false, length = 255)
  private String filePath;

  @Size(max = 50)
  @Pattern(regexp = "^(jpg|png|pdf|docx)$", message = "File type không hợp lệ")
  @Column(name = "file_type", length = 50)
  private String fileType;

  @CreatedDate
  @Column(name = "uploaded_at", nullable = false, updatable = false)
  private LocalDateTime uploadedAt;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
