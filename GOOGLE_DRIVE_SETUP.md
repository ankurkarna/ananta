# Google Drive Integration Setup Guide

This guide walks you through setting up Google Drive API integration for automatic file uploads in the Ananta social media application.

## Overview

The application automatically uploads user photos/videos to Google Drive with a graceful fallback to local storage if Google Drive is not configured. This allows you to leverage your 2TB Google Drive storage without users knowing about it.

## Prerequisites

- Google account
- Google Cloud Platform access
- Your Ananta application running

## Step-by-Step Setup

### 1. Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click on the project dropdown at the top
3. Click **"New Project"**
4. Enter project name: `Ananta Social Media` (or your preferred name)
5. Click **"Create"**

### 2. Enable Google Drive API

1. In the Google Cloud Console, make sure your new project is selected
2. Go to **"APIs & Services"** > **"Library"**
3. Search for **"Google Drive API"**
4. Click on it and press **"Enable"**

### 3. Create Service Account

A service account allows your backend to authenticate with Google Drive without user interaction.

1. Go to **"APIs & Services"** > **"Credentials"**
2. Click **"Create Credentials"** > **"Service Account"**
3. Fill in the details:
   - **Service account name**: `ananta-drive-uploader`
   - **Service account ID**: (auto-generated)
   - **Description**: `Service account for uploading files to Google Drive`
4. Click **"Create and Continue"**
5. For **"Grant this service account access to project"**:
   - Skip this step, click **"Continue"**
6. For **"Grant users access to this service account"**:
   - Skip this step, click **"Done"**

### 4. Create and Download Service Account Key

1. In the **"Credentials"** page, find your newly created service account
2. Click on the service account email
3. Go to the **"Keys"** tab
4. Click **"Add Key"** > **"Create new key"**
5. Select **"JSON"** format
6. Click **"Create"**
7. The JSON key file will be downloaded to your computer
8. **Important**: Keep this file secure! It provides access to your Google Drive

### 5. Configure Your Application

#### Option A: Place credentials in project root (Recommended for development)

1. Rename the downloaded JSON file to `credentials.json`
2. Place it in your project root directory: `/home/user/ananta/credentials.json`
3. Add to `.gitignore` to prevent committing sensitive data:
   ```bash
   echo "credentials.json" >> .gitignore
   ```

#### Option B: Custom path

1. Place the JSON file anywhere on your server
2. Update `application.yml`:
   ```yaml
   google:
     drive:
       credentials:
         path: /path/to/your/credentials.json
   ```

### 6. Create Google Drive Folder (Optional but Recommended)

To organize uploaded files in a specific folder:

1. Go to [Google Drive](https://drive.google.com)
2. Create a new folder: **"Ananta Uploads"** (or your preferred name)
3. Right-click the folder > **"Share"**
4. Add the service account email (found in credentials.json under `client_email`)
5. Give it **"Editor"** permissions
6. Click **"Share"**
7. Open the folder in Google Drive
8. Copy the folder ID from the URL:
   - URL format: `https://drive.google.com/drive/folders/FOLDER_ID_HERE`
   - Copy the `FOLDER_ID_HERE` part
9. Update `application.yml`:
   ```yaml
   google:
     drive:
       credentials:
         path: credentials.json
       folder:
         id: YOUR_FOLDER_ID_HERE
   ```

### 7. Verify Configuration

Your final `application.yml` should include:

```yaml
google:
  drive:
    credentials:
      path: credentials.json  # or full path
    folder:
      id: 1a2b3c4d5e6f7g8h9i0j  # optional, your folder ID
```

### 8. Test the Integration

1. Start your Spring Boot application
2. Check logs for:
   ```
   Google Drive not configured, using local storage
   ```
   If you see this, the credentials file wasn't found.

3. Upload a test file via the frontend
4. Check logs for:
   ```
   File uploaded to Google Drive: https://drive.google.com/uc?export=view&id=...
   ```

5. Verify in your Google Drive folder that the file appears

## How It Works

### Backend Flow

1. User uploads file via `/api/upload/image` endpoint
2. `FileUploadController` receives the file
3. Checks if Google Drive is configured (`credentials.json` exists)
4. If configured:
   - Uploads to Google Drive via `GoogleDriveService`
   - Makes file public (anyone with link can view)
   - Returns direct view URL: `https://drive.google.com/uc?export=view&id=FILE_ID`
5. If upload fails or not configured:
   - Falls back to local storage
   - Stores in `uploads/` directory
   - Returns local URL: `/uploads/filename.jpg`

### File Naming

Files are renamed on upload with UUID prefix to prevent conflicts:
```
UUID-original-filename.jpg
Example: 550e8400-e29b-41d4-a716-446655440000-vacation.jpg
```

### Permissions

All uploaded files are automatically made public with "anyone with the link can view" permission. This allows the frontend to display images without authentication.

## Security Considerations

### Credentials Security

- **Never commit `credentials.json` to version control**
- Add to `.gitignore` immediately
- For production, use environment variables or secret management:
  ```yaml
  google:
    drive:
      credentials:
        path: ${GOOGLE_DRIVE_CREDENTIALS_PATH:/app/config/credentials.json}
  ```

### File Validation

The application validates:
- File is not empty
- File type is image or video
- File size is less than 100MB

### Public Access

Files are made public by default. If you need private files:
1. Remove the permission creation code in `GoogleDriveService.java:68-71`
2. Implement signed URL generation for temporary access

## Troubleshooting

### "Google Drive not configured"
- Check `credentials.json` exists at the specified path
- Verify the path in `application.yml` is correct
- Check file permissions (application must be able to read it)

### "Failed to upload file to Google Drive"
- Verify Google Drive API is enabled in Google Cloud Console
- Check service account has permission to the folder
- Review service account key is valid (not expired or revoked)
- Check application logs for detailed error messages

### "File uploads but returns 403 on view"
- Ensure permissions are set correctly (public access)
- Verify the file ID in the returned URL is correct
- Check the file exists in Google Drive

### Files appear in Drive but not in specific folder
- Verify folder ID is correct in `application.yml`
- Ensure service account has "Editor" permission on the folder
- Check folder wasn't deleted or moved

## Storage Limits

- Google Drive Free: 15 GB
- Google Workspace plans: 30 GB - 2 TB+
- Monitor storage usage in [Google Drive Storage](https://one.google.com/storage)

## Production Deployment

For production environments:

1. **Use environment variables**:
   ```yaml
   google:
     drive:
       credentials:
         path: ${GOOGLE_CREDENTIALS_PATH}
       folder:
         id: ${GOOGLE_DRIVE_FOLDER_ID}
   ```

2. **Secure credentials**:
   - Use secret management (AWS Secrets Manager, Azure Key Vault, etc.)
   - Mount credentials as read-only volume
   - Restrict file permissions: `chmod 600 credentials.json`

3. **Monitor usage**:
   - Set up Google Cloud monitoring
   - Track API quotas
   - Monitor storage consumption

4. **Backup strategy**:
   - Google Drive doesn't replace proper backups
   - Consider periodic exports
   - Maintain metadata in your database

## Alternative: User's Own Google Drive

If you want users to upload to their own Google Drive:

1. Implement OAuth 2.0 flow instead of service accounts
2. Request Drive API scopes during user authentication
3. Store user refresh tokens securely
4. Upload files using user's credentials

This requires significant changes and user consent for Drive access.

## Support

For issues related to:
- **Google Cloud**: Check [Google Cloud Support](https://cloud.google.com/support)
- **Drive API**: See [Drive API Documentation](https://developers.google.com/drive/api/guides/about-sdk)
- **Application**: Check application logs and Spring Boot configuration

## Summary

Once configured, the system will:
- ✅ Automatically upload all user files to your Google Drive
- ✅ Make them publicly accessible
- ✅ Fall back to local storage if Drive fails
- ✅ Work transparently without user knowledge
- ✅ Support images and videos up to 100MB
- ✅ Organize files by UUID to prevent conflicts

Your 2TB Google Drive storage is now powering your social media application!
