# Application Configuration Setup

## Quick Fix for File Upload Issues

If you're experiencing:
- ❌ **"File size limit too low"** (uploads fail for files > 1MB)
- ❌ **"Images not displaying"** in the app after upload

Follow these steps:

## Step 1: Update Your application.yml

Your `application.yml` is gitignored, so you need to add this configuration manually.

**Location**: `src/main/resources/application.yml`

**Add this configuration** (or update existing):

```yaml
spring:
  servlet:
    multipart:
      enabled: true
      max-file-size: 100MB      # Maximum file size for images/videos
      max-request-size: 105MB   # Maximum request size
      file-size-threshold: 2MB  # Buffering threshold
```

### Complete Example

I've created `application-example.yml` in the project root with a complete configuration template. You can:

**Option 1: Copy for local development**
```bash
cp application-example.yml src/main/resources/application-local.yml
# Then edit application-local.yml with your settings
```

**Option 2: Update existing application.yml**
```bash
# Add the multipart configuration to your existing application.yml
```

## Step 2: Restart Your Application

After updating the configuration:
```bash
# Stop your application (Ctrl+C)
# Restart it
./mvnw spring-boot:run
```

## What Was Fixed

### 1. Spring Boot File Size Limit (Fixed in Configuration)

**Problem**:
- Spring Boot's default multipart file size limit is **1MB**
- Any file larger than 1MB was rejected before reaching your code

**Solution**:
```yaml
spring.servlet.multipart.max-file-size: 100MB
spring.servlet.multipart.max-request-size: 105MB
```

**Now supports**:
- ✅ Images up to 100MB
- ✅ Videos up to 100MB
- ✅ Proper error messages if exceeded

### 2. Google Drive Image Display (Fixed in Code)

**Problem**:
- Previous URL format: `https://drive.google.com/uc?export=view&id=FILE_ID`
- This format has CORS issues and doesn't always work for embedding

**Solution**:
- New URL format: `https://lh3.googleusercontent.com/d/FILE_ID`
- Uses Google's CDN (lh3.googleusercontent.com)
- Better for direct image embedding
- No CORS issues
- Faster loading

**Examples**:
```
Before: https://drive.google.com/uc?export=view&id=1zEAtAi8Oe-Dqc4Vwv5_LqT2a_2PY5EHO
After:  https://lh3.googleusercontent.com/d/1zEAtAi8Oe-Dqc4Vwv5_LqT2a_2PY5EHO
```

## Testing

### Test File Upload

1. Restart your application with updated configuration
2. Upload a test image through your frontend
3. Check the logs for the returned URL:
   ```
   File uploaded to Google Drive: https://lh3.googleusercontent.com/d/FILE_ID
   ```
4. Verify the image displays in your app

### Test Large Files

Upload files of various sizes to verify:
- ✅ Small images (< 1MB): Work
- ✅ Medium images (1-10MB): Work
- ✅ Large images (10-50MB): Work
- ✅ Videos (up to 100MB): Work

## Configuration Properties Explained

### Multipart File Upload

| Property | Default | Recommended | Description |
|----------|---------|-------------|-------------|
| `max-file-size` | 1MB | 100MB | Maximum size for a single file |
| `max-request-size` | 10MB | 105MB | Maximum size for entire request |
| `file-size-threshold` | 0 | 2MB | Size above which files are written to disk |

**Why 105MB for request size?**
- Request includes: file data + JSON metadata + headers
- Set slightly higher than max-file-size to account for overhead

### File Size Threshold

Files smaller than this threshold are kept in memory. Larger files are written to a temporary directory.

**Trade-offs**:
- Low threshold (0): All files written to disk → Slower but uses less memory
- High threshold (10MB): More files in memory → Faster but uses more memory
- **Recommended (2MB)**: Good balance for typical images

## Troubleshooting

### "Maximum upload size exceeded"

If you still see this error after configuration:

1. **Check application.yml location**:
   - Must be in `src/main/resources/application.yml`
   - Not in project root

2. **Check YAML syntax**:
   ```yaml
   spring:  # No extra spaces before 'spring'
     servlet:  # Two spaces indent
       multipart:  # Two spaces indent
         max-file-size: 100MB  # Two spaces indent
   ```

3. **Restart application**:
   - Configuration only loads at startup
   - Must restart after changes

### "Images still not displaying"

1. **Check browser console** for errors:
   - Right-click → Inspect → Console tab
   - Look for CORS errors or 403 errors

2. **Verify file permissions** in Google Drive:
   - File should be public (anyone with link)
   - This is automatic, but verify in Drive

3. **Test URL directly**:
   - Copy the returned URL
   - Paste in new browser tab
   - Image should display directly

4. **Check image URL format**:
   ```
   Correct: https://lh3.googleusercontent.com/d/FILE_ID
   Wrong:   https://drive.google.com/file/d/FILE_ID/view
   ```

### Frontend CORS Issues

If using Vite for frontend, verify proxy configuration in `vite.config.js`:

```javascript
export default defineConfig({
  server: {
    proxy: {
      '/ananta/dev': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      }
    }
  }
})
```

## Production Considerations

### Nginx/Apache Reverse Proxy

If deploying behind a reverse proxy, also configure the proxy's upload limit:

**Nginx**:
```nginx
client_max_body_size 100M;
```

**Apache**:
```apache
LimitRequestBody 104857600
```

### Cloud Deployment

For cloud platforms, check their upload limits:

- **AWS Elastic Beanstalk**: Update nginx configuration
- **Heroku**: 30MB hard limit (use external storage)
- **Google Cloud Run**: 32MB request limit (use Google Drive as we do)
- **Azure App Service**: Configure in portal or web.config

### Database Considerations

Since we store URLs (not files) in the database:
- No database size concerns
- VARCHAR(500) is sufficient for Google Drive URLs
- Example URL length: ~60 characters

## Summary of Changes

### Code Changes (GoogleDriveService.java)
```java
// Before
return "https://drive.google.com/uc?export=view&id=" + uploadedFile.getId();

// After
return "https://lh3.googleusercontent.com/d/" + uploadedFile.getId();
```

### Configuration Required (application.yml)
```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB
      max-request-size: 105MB
      file-size-threshold: 2MB
```

### Result
- ✅ Images up to 100MB supported
- ✅ Videos up to 100MB supported
- ✅ Images display correctly in web app
- ✅ Fast loading via Google CDN
- ✅ No CORS issues

## Next Steps

1. Update `application.yml` with multipart configuration
2. Restart your Spring Boot application
3. Test uploading various file sizes
4. Verify images display in the frontend
5. Enjoy your 2TB Google Drive storage! 🎉
