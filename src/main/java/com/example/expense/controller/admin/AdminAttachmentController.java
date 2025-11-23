package com.example.expense.controller.admin;

import com.example.expense.entity.Attachment;
import com.example.expense.exception.ResourceNotFoundException;
import com.example.expense.repository.AttachmentRepository;
import com.example.expense.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/attachments")
public class AdminAttachmentController {

  private final AttachmentRepository attachmentRepository;
  private final FileStorageService fileStorageService;

  @GetMapping("/download/{id}")
  public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id) {
    Attachment attachment =
        attachmentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

    Resource resource = fileStorageService.loadAsResource(attachment.getFilePath());

    return ResponseEntity.ok()
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + attachment.getFileName() + "\"")
        .body(resource);
  }

  @DeleteMapping("/delete/{id}")
  @ResponseBody
  public ResponseEntity<?> deleteAttachment(@PathVariable Long id) {
    Attachment attachment =
        attachmentRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attachment not found"));

    try {
      fileStorageService.deleteFile(attachment.getFilePath());
      attachmentRepository.delete(attachment);
      return ResponseEntity.ok().body("File deleted successfully");
    } catch (RuntimeException e) {
      return ResponseEntity.status(500).body("Could not delete file: " + e.getMessage());
    }
  }
}
