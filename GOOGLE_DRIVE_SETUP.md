# Google Drive Integration Setup Guide

This guide walks you through setting up Google Drive API integration for automatic file uploads in the Ananta social media application using **your personal 2TB Google Drive** account.

## Overview

The application automatically uploads user photos/videos to **your Google Drive** (the one with 2TB storage) with a graceful fallback to local storage if Google Drive is not configured. This allows you to leverage your existing Google Drive storage without users knowing about it.

## Important: OAuth 2.0 vs Service Account

**We use OAuth 2.0 (not Service Account)** because:
- ✅ Service accounts don't have storage quota
- ✅ OAuth 2.0 uses YOUR personal Google Drive storage (2TB)
- ✅ Files are uploaded to your Drive account
- ✅ One-time authorization flow

## Prerequisites

- Google account with 2TB storage
- Google Cloud Platform access
- Your Ananta application running locally

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

### 3. Configure OAuth Consent Screen

1. Go to **"APIs & Services"** > **"OAuth consent screen"**
2. Select **"External"** user type (unless you have Google Workspace)
3. Click **"Create"**
4. Fill in the required information:
   - **App name**: `Ananta Social Media`
   - **User support email**: Your email
   - **Developer contact information**: Your email
5. Click **"Save and Continue"**
6. **Scopes**: Skip this section, click **"Save and Continue"**
7. **Test users**: Add your Google account email (the one with 2TB storage)
   - Click **"Add Users"**
   - Enter your email
   - Click **"Add"**
8. Click **"Save and Continue"**
9. Review and click **"Back to Dashboard"**

### 4. Create OAuth 2.0 Credentials

1. Go to **"APIs & Services"** > **"Credentials"**
2. Click **"Create Credentials"** > **"OAuth client ID"**
3. If prompted, configure the consent screen (you already did this)
4. For **Application type**, select **"Desktop app"**
5. **Name**: `Ananta Desktop Client`
6. Click **"Create"**
7. A dialog will appear showing your Client ID and Client Secret
8. Click **"Download JSON"**
9. **Important**: Save this file!

### 5. Configure Your Application

1. Rename the downloaded JSON file to `credentials.json`
2. Place it in your project root directory: `/home/user/ananta/credentials.json`
   - The file should be in the same directory as `pom.xml`
3. Verify `.gitignore` contains `credentials.json` (to prevent committing sensitive data)

**Your `credentials.json` should look like this:**
```json
{
  "installed": {
    "client_id": "YOUR_CLIENT_ID.apps.googleusercontent.com",
    "project_id": "ananta-social-media",
    "auth_uri": "https://accounts.google.com/o/oauth2/auth",
    "token_uri": "https://oauth2.googleapis.com/token",
    "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
    "client_secret": "YOUR_CLIENT_SECRET",
    "redirect_uris": ["http://localhost"]
  }
}
```

### 6. First-Time Authorization

**The first time the application uploads a file to Google Drive, you'll need to authorize it:**

1. Start your Spring Boot application
2. Upload a test file through the frontend or use Postman to test `/api/upload/image`
3. **A browser window will automatically open** asking you to sign in to Google
4. Sign in with **your Google account** (the one with 2TB storage)
5. You'll see a warning: "Google hasn't verified this app"
   - Click **"Advanced"**
   - Click **"Go to Ananta Social Media (unsafe)"**
6. Review the permissions:
   - "See, edit, create, and delete only the specific Google Drive files you use with this app"
7. Click **"Continue"**
8. You'll see a success message: "The authentication flow has completed"
9. **Close the browser window**
10. The file upload will complete automatically

**This authorization only needs to be done ONCE.** The refresh token is saved in the `tokens/` directory.

### 7. Create Google Drive Folder (Optional but Recommended)

To organize uploaded files in a specific folder:

1. Go to [Google Drive](https://drive.google.com)
2. Create a new folder: **"Ananta Uploads"** (or your preferred name)
3. Open the folder in Google Drive
4. Copy the folder ID from the URL:
   - URL format: `https://drive.google.com/drive/folders/FOLDER_ID_HERE`
   - Copy the `FOLDER_ID_HERE` part
5. Update `application.yml` (or create `application-local.yml`):
   ```yaml
   google:
     drive:
       folder:
         id: YOUR_FOLDER_ID_HERE
   ```

**Note**: You can create `application-local.yml` in `src/main/resources/` for local development without affecting git.

### 8. Verify Configuration

Check your directory structure:
```
/home/user/ananta/
├── credentials.json          ← Your OAuth credentials (gitignored)
├── tokens/                   ← Created automatically after first auth (gitignored)
│   └── StoredCredential      ← Your refresh token
├── pom.xml
├── src/
└── ...
```

### 9. Test the Integration

1. Start your Spring Boot application
2. Check logs at startup:
   - No warnings = credentials.json found
3. Upload a test file via the frontend
4. **First time only**: Browser opens for authorization (see step 6)
5. After authorization, check logs for:
   ```
   File uploaded to Google Drive: uuid-filename.jpg (ID: FILE_ID)
   ```
6. Verify in your Google Drive that the file appears
   - If you configured a folder ID, it will be in that folder
   - Otherwise, it will be in "My Drive" root

## How It Works

### Backend Flow

1. User uploads file via `/api/upload/image` endpoint
2. `FileUploadController` receives the file
3. Checks if Google Drive is configured (`credentials.json` exists)
4. If configured:
   - First time: Opens browser for OAuth authorization
   - Subsequent times: Uses stored refresh token
   - Uploads to **your Google Drive** via `GoogleDriveService`
   - Makes file public (anyone with link can view)
   - Returns direct view URL: `https://drive.google.com/uc?export=view&id=FILE_ID`
5. If upload fails or not configured:
   - Falls back to local storage
   - Stores in `uploads/` directory
   - Returns local URL: `/uploads/filename.jpg`

### OAuth 2.0 Flow

1. **First upload**:
   - Application reads `credentials.json`
   - Opens browser for Google sign-in
   - User authorizes the application
   - Google returns refresh token
   - Application saves token to `tokens/StoredCredential`

2. **Subsequent uploads**:
   - Application reads refresh token from `tokens/`
   - Automatically gets new access token
   - Uploads file to Drive
   - No browser interaction needed

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
- **Never commit `tokens/` directory to version control**
- Both are already in `.gitignore`
- For production, use environment variables or secret management

### OAuth Scopes

The application requests minimal scope:
- `https://www.googleapis.com/auth/drive.file`
- This only allows access to files created by the app
- NOT full Google Drive access

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
- Check `credentials.json` exists in project root (same level as `pom.xml`)
- Verify it's a valid JSON file with OAuth credentials (not service account)
- Check the file structure matches the example in Step 5

### "Failed to upload file to Google Drive: 403 Forbidden" with service account message
- You're using service account credentials instead of OAuth credentials
- Delete `credentials.json`
- Follow Step 4 again and select **"Desktop app"** (not Service account)

### Browser doesn't open for authorization
- Check if port 8888 is available
- Check application logs for authorization URL
- Manually open the URL in your browser
- Complete authorization and check `tokens/` directory

### "The authentication flow has completed" but upload still fails
- Check `tokens/` directory exists with `StoredCredential` file
- Restart your application
- Try uploading again (shouldn't need browser this time)

### Files upload but return 403 on view
- Ensure permissions are set correctly (public access)
- Check the returned URL format
- Verify the file exists in your Google Drive

### "This app is blocked" during authorization
- This happens if you didn't add yourself as a test user
- Go to OAuth consent screen > Test users
- Add your email address
- Try authorization again

## Storage Limits

Your uploads use **your personal Google Drive quota**:
- Free: 15 GB
- Google One plans: 100 GB, 200 GB, 2 TB
- Monitor storage at [Google Drive Storage](https://one.google.com/storage)

## Production Deployment

For production environments:

### 1. Server without Display (Headless)

The current implementation requires a browser for first-time authorization. For headless servers:

**Option A**: Authorize locally first
1. Run the app locally with same credentials
2. Complete browser authorization
3. Copy `tokens/` directory to production server
4. Application will use the saved token

**Option B**: Manual authorization flow (advanced)
1. Implement manual authorization with redirect URI
2. Deploy with authorization endpoint
3. User (you) visits the endpoint once
4. Tokens saved for future use

### 2. Environment Variables

Instead of hardcoded paths:
```yaml
google:
  drive:
    credentials:
      path: ${GOOGLE_CREDENTIALS_PATH:/app/config/credentials.json}
    folder:
      id: ${GOOGLE_DRIVE_FOLDER_ID:}
```

### 3. Secure Credentials

- Use secret management (AWS Secrets Manager, Azure Key Vault, etc.)
- Mount credentials as read-only volume
- Restrict file permissions: `chmod 600 credentials.json tokens/*`

### 4. Token Refresh

The application automatically refreshes access tokens using the refresh token. No manual intervention needed unless:
- Refresh token is revoked (user revokes access)
- Token expires after 6 months of inactivity
- Re-authorization required (repeat Step 6)

## Revoking Access

To revoke the application's access to your Google Drive:

1. Go to [Google Account Security](https://myaccount.google.com/permissions)
2. Find "Ananta Social Media"
3. Click **"Remove Access"**
4. Delete `tokens/` directory in your project
5. Next upload will require re-authorization

## Alternative Approaches

### Using a Different Google Account

If you want to use a different account:
1. Delete `tokens/` directory
2. Upload a file (triggers authorization)
3. Sign in with the different account
4. Make sure that account is added as a test user in OAuth consent screen

### Shared Drive (Google Workspace Only)

If you have Google Workspace with Shared Drives:
1. Create a Shared Drive
2. Upload files to the Shared Drive folder
3. Storage counts against Workspace quota, not personal quota

## Summary

Once configured, the system will:
- ✅ Automatically upload all user files to YOUR Google Drive (2TB)
- ✅ First-time authorization via browser (one-time)
- ✅ Automatic token refresh (no re-auth needed)
- ✅ Make files publicly accessible
- ✅ Fall back to local storage if Drive fails
- ✅ Work transparently without user knowledge
- ✅ Support images and videos up to 100MB
- ✅ Organize files by UUID to prevent conflicts

Your 2TB Google Drive storage is now powering your social media application!

## Support

For issues related to:
- **Google Cloud**: Check [Google Cloud Support](https://cloud.google.com/support)
- **Drive API**: See [Drive API Documentation](https://developers.google.com/drive/api/guides/about-sdk)
- **OAuth 2.0**: See [OAuth 2.0 Guide](https://developers.google.com/identity/protocols/oauth2)
- **Application**: Check application logs and Spring Boot configuration
