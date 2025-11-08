package org.karna.ankur.ananta.controller;

import lombok.RequiredArgsConstructor;
import org.karna.ankur.ananta.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

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

            // Store file
            String fileUrl = fileStorageService.storeFile(file);

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("message", "File uploaded successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to upload file: " + e.getMessage()));
        }
    }
}
