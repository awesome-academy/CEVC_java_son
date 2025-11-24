package com.example.expense.entity;

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

  @NotBlank
  @Size(max = 255)
  @Column(name = "file_name", nullable = false, length = 255)
  private String fileName;

  @NotBlank
  @Size(max = 255)
  @Column(name = "file_path", nullable = false, length = 255)
  private String filePath;

  @Size(max = 50)
  @Pattern(
      regexp =
          "^(image/jpeg|image/png|application/pdf|application/vnd.openxmlformats-officedocument.wordprocessingml.document)$",
      message = "error.invalid_file_type")
  @Column(name = "file_type", length = 50)
  private String fileType;

  @CreatedDate
  @Column(name = "uploaded_at", nullable = false, updatable = false)
  private LocalDateTime uploadedAt;

  @Column(name = "attachmentable_type", nullable = false, length = 20)
  private String attachmentableType;

  @Column(name = "attachmentable_id", nullable = false)
  private Long attachmentableId;
}
