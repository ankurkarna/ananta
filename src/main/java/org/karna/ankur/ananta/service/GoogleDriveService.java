package org.karna.ankur.ananta.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

@Service
@Slf4j
public class GoogleDriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Ananta Social Media";

    @Value("${google.drive.credentials.path:credentials.json}")
    private String credentialsPath;

    @Value("${google.drive.folder.id:}")
    private String folderId;

    /**
     * Uploads file to Google Drive and returns public URL
     */
    public String uploadFile(MultipartFile file) throws IOException, GeneralSecurityException {
        // Create temporary file
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        try {
            // Build Drive service
            Drive driveService = getDriveService();

            // Create file metadata
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            File fileMetadata = new File();
            fileMetadata.setName(fileName);

            // Set parent folder if configured
            if (folderId != null && !folderId.isEmpty()) {
                fileMetadata.setParents(Collections.singletonList(folderId));
            }

            // Upload file
            FileContent mediaContent = new FileContent(file.getContentType(), tempFile.toFile());
            File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                    .setFields("id, webViewLink, webContentLink")
                    .execute();

            // Make file public
            Permission permission = new Permission();
            permission.setType("anyone");
            permission.setRole("reader");
            driveService.permissions().create(uploadedFile.getId(), permission).execute();

            log.info("File uploaded to Google Drive: {} (ID: {})", fileName, uploadedFile.getId());

            // Return direct view URL
            return "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();

        } finally {
            // Clean up temp file
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Deletes file from Google Drive
     */
    public void deleteFile(String fileId) throws IOException, GeneralSecurityException {
        Drive driveService = getDriveService();
        driveService.files().delete(fileId).execute();
        log.info("File deleted from Google Drive: {}", fileId);
    }

    /**
     * Creates Drive service with credentials
     */
    private Drive getDriveService() throws IOException, GeneralSecurityException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                .createScoped(Collections.singletonList("https://www.googleapis.com/auth/drive.file"));

        return new Drive.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Check if Google Drive is configured
     */
    public boolean isConfigured() {
        try {
            java.io.File credFile = new java.io.File(credentialsPath);
            return credFile.exists();
        } catch (Exception e) {
            return false;
        }
    }
}
