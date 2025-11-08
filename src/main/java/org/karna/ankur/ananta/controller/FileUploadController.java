package org.karna.ankur.ananta.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.karna.ankur.ananta.service.FileStorageService;
import org.karna.ankur.ananta.service.GoogleDriveService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class FileUploadController {

    private final FileStorageService fileStorageService;
    private final GoogleDriveService googleDriveService;

    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
            }

            if (!fileStorageService.isImage(file) && !fileStorageService.isVideo(file)) {
                return ResponseEntity.badRequest().body(Map.of("error", "File must be an image or video"));
            }

            if (!fileStorageService.isValidSize(file)) {
                return ResponseEntity.badRequest().body(Map.of("error", "File size must be less than 100MB"));
            }

            String fileUrl;
            String storageType;

            // Try Google Drive first, fall back to local storage
            if (googleDriveService.isConfigured()) {
                try {
                    fileUrl = googleDriveService.uploadFile(file);
                    storageType = "Google Drive";
                    log.info("File uploaded to Google Drive: {}", fileUrl);
                } catch (Exception e) {
                    log.warn("Google Drive upload failed, falling back to local storage: {}", e.getMessage());
                    fileUrl = fileStorageService.storeFile(file);
                    storageType = "Local Storage";
                }
            } else {
                fileUrl = fileStorageService.storeFile(file);
                storageType = "Local Storage";
                log.info("Google Drive not configured, using local storage");
            }

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("message", "File uploaded successfully to " + storageType);
            response.put("storage", storageType);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("File upload error: ", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }
}
