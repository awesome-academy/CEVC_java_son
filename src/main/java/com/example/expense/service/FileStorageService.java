package com.example.expense.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

  private final Path fileStorageLocation;

  public FileStorageService(@Value("${file.upload-dir}") String uploadDir) {
    this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    try {
      Files.createDirectories(this.fileStorageLocation);
    } catch (Exception ex) {
      throw new RuntimeException("Could not create upload directory", ex);
    }
  }

  public String storeFile(MultipartFile file) {
    String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
    String fileName = UUID.randomUUID() + "_" + originalFileName;

    try {
      if (originalFileName.contains("..")) {
        throw new RuntimeException("Invalid file path sequence " + originalFileName);
      }

      Path targetLocation = this.fileStorageLocation.resolve(fileName);
      Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

      return fileName;
    } catch (IOException ex) {
      throw new RuntimeException("Could not store file " + fileName, ex);
    }
  }

  public Path loadFile(String fileName) {
    return this.fileStorageLocation.resolve(fileName).normalize();
  }

  public void deleteFile(String fileName) {
    try {
      Path filePath = loadFile(fileName);
      Files.deleteIfExists(filePath);
    } catch (IOException ex) {
      throw new RuntimeException("Could not delete file " + fileName, ex);
    }
  }

  public Resource loadAsResource(String filename) {
    try {
      Path file = fileStorageLocation.resolve(filename).normalize();
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() && resource.isReadable()) {
        return resource;
      } else {
        throw new RuntimeException("Could not read file: " + filename);
      }
    } catch (MalformedURLException e) {
      throw new RuntimeException("Could not read file: " + filename, e);
    }
  }
}
