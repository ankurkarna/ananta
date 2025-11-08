package org.karna.ankur.ananta.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class GoogleDriveService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "Ananta Social Media";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

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

            // Return direct image URL using Google's CDN (better for embedding in web apps)
            // This format works better for CORS and direct image display
            return "https://lh3.googleusercontent.com/d/" + uploadedFile.getId();

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
     * Creates Drive service with OAuth 2.0 credentials
     */
    private Drive getDriveService() throws IOException, GeneralSecurityException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // Load client secrets from credentials.json
        GoogleClientSecrets clientSecrets;
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(credentialsPath))) {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, reader);
        }

        // Build flow and trigger user authorization request
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        // Create receiver for authorization code
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

        // Authorize and get credential
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Check if Google Drive is configured (credentials exist)
     */
    public boolean isConfigured() {
        try {
            java.io.File credFile = new java.io.File(credentialsPath);
            return credFile.exists();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if user has authorized the application (tokens exist)
     */
    public boolean isAuthorized() {
        try {
            java.io.File tokensDir = new java.io.File(TOKENS_DIRECTORY_PATH);
            if (!tokensDir.exists()) {
                return false;
            }
            java.io.File[] tokenFiles = tokensDir.listFiles();
            return tokenFiles != null && tokenFiles.length > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
