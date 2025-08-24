package com.namhatta.controller;

import com.namhatta.dto.FileUploadResponse;
import com.namhatta.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileStorageService fileStorageService;
    
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            log.info("File upload request received: {}", file.getOriginalFilename());
            
            // Validate file
            if (file.isEmpty()) {
                log.warn("Empty file upload attempt");
                return ResponseEntity.badRequest()
                    .body(new FileUploadResponse(false, "File is empty", null, null));
            }
            
            // Check file size
            if (file.getSize() > MAX_FILE_SIZE) {
                log.warn("File size exceeds limit: {} bytes", file.getSize());
                return ResponseEntity.badRequest()
                    .body(new FileUploadResponse(false, "File size exceeds 5MB limit", null, null));
            }
            
            // Check content type
            if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
                log.warn("Invalid file type: {}", file.getContentType());
                return ResponseEntity.badRequest()
                    .body(new FileUploadResponse(false, "Only image files (jpg, png, gif, webp) are allowed", null, null));
            }
            
            // Store file
            String fileName = fileStorageService.storeFile(file);
            String fileUrl = "/api/files/" + fileName;
            
            log.info("File uploaded successfully: {}", fileName);
            return ResponseEntity.ok(new FileUploadResponse(true, "File uploaded successfully", fileName, fileUrl));
            
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(new FileUploadResponse(false, "Error uploading file: " + e.getMessage(), null, null));
        } catch (Exception e) {
            log.error("Unexpected error during file upload: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                .body(new FileUploadResponse(false, "Unexpected error occurred", null, null));
        }
    }
    
    @GetMapping("/{fileName:.+}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try {
            return fileStorageService.loadFileAsResource(fileName);
        } catch (Exception e) {
            log.error("Error downloading file {}: {}", fileName, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
    }
}