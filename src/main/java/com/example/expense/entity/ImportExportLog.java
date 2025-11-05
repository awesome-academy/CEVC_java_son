package com.example.expense.entity;

import com.example.expense.enums.ImportExportAction;
import com.example.expense.enums.ImportExportStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "import_export_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ImportExportLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 36)
  @Column(nullable = false, unique = true, length = 36)
  private String uuid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @JsonBackReference
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(length = 10)
  private ImportExportAction action;

  @Size(max = 20)
  @Column(name = "target_type", length = 20)
  private String targetType;

  @Size(max = 255)
  @Column(name = "file_name", length = 255)
  private String fileName;

  @Enumerated(EnumType.STRING)
  @Column(length = 10)
  private ImportExportStatus status;

  @Lob private String message;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
