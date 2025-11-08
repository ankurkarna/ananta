# 📁 Google Drive Integration Guide

This guide explains how to use your Google Drive (2TB) for storing images and videos in Ananta.

---

## 🎯 Three Ways to Add Images/Videos

### **Option 1: Upload Files Directly (Local Storage)**
✅ Easiest method
✅ No Google Drive setup needed
✅ Files stored on your server

**How to use:**
1. Click the ➕ button to create a post
2. Select **"Upload File"** tab
3. Choose an image or video (max 100MB)
4. Add caption and click "Post"

**Files are saved in:** `/home/user/ananta/uploads/`

---

### **Option 2: Google Drive Sharing Links (Recommended for 2TB storage)**
✅ Unlimited storage (uses your 2TB)
✅ No server storage needed
✅ Super easy - just share and paste!

**How to use:**

#### Step 1: Upload to Google Drive
1. Go to [Google Drive](https://drive.google.com)
2. Upload your image/video

#### Step 2: Get Sharing Link
1. Right-click the file
2. Click **"Share"** → **"Get link"**
3. Set to **"Anyone with the link"** can view
4. Copy the link

#### Step 3: Paste in Ananta
1. Click ➕ to create post
2. Select **"URL / Google Drive"** tab
3. Paste the link (e.g., `https://drive.google.com/file/d/1ABC...XYZ/view?usp=sharing`)
4. Add caption and click "Post"

**✨ The backend automatically converts this to a direct image link!**

---

### **Option 3: Any Image URL**
Use any publicly accessible image URL from the web.

---

## 🔧 Google Drive Link Formats

### What you copy from Google Drive:
```
https://drive.google.com/file/d/1ABC123XYZ456/view?usp=sharing
```

### What Ananta converts it to (automatically):
```
https://drive.google.com/uc?export=view&id=1ABC123XYZ456
```

This conversion happens automatically in the backend! 🎉

---

## 📋 Step-by-Step: Using Google Drive for Posts

### **Method A: Using the Web Interface**

1. **Upload to Google Drive:**
   - Go to https://drive.google.com
   - Create a folder called "Ananta Posts" (optional but organized)
   - Upload your image/video

2. **Make it Public:**
   - Right-click the file
   - Click "Share"
   - Change from "Restricted" to "Anyone with the link"
   - Click "Copy link"

3. **Create Post in Ananta:**
   - Open Ananta frontend (http://localhost:3000)
   - Click ➕ (Create Post button)
   - Select **"URL / Google Drive"** tab
   - Paste the Google Drive link
   - Add your caption
   - Click "Post"

---

## 🎨 Supported File Types

### Images:
- JPG/JPEG
- PNG
- GIF
- WEBP
- BMP

### Videos:
- MP4
- MOV
- AVI
- WEBM

**Max file size:** 100MB (for local uploads)
**Google Drive:** Up to your storage limit (2TB!)

---

## 🚀 Backend Auto-Conversion

The backend automatically detects and converts Google Drive links:

```java
// In PostService.java
if (googleDriveUtil.isGoogleDriveLink(imageUrl)) {
    imageUrl = googleDriveUtil.convertToDirectLink(imageUrl);
}
```

**Supported Google Drive URL formats:**
- `https://drive.google.com/file/d/FILE_ID/view?usp=sharing`
- `https://drive.google.com/file/d/FILE_ID/view`
- `https://drive.google.com/open?id=FILE_ID`

All are converted to: `https://drive.google.com/uc?export=view&id=FILE_ID`

---

## 💡 Pro Tips

### **For Best Organization:**

1. **Create Folders in Google Drive:**
   ```
   My Drive/
   ├── Ananta Posts/
   │   ├── 2024/
   │   │   ├── January/
   │   │   ├── February/
   │   │   └── ...
   ```

2. **Name Files Descriptively:**
   - ✅ `sunset-beach-2024-01-15.jpg`
   - ❌ `IMG_1234.jpg`

3. **Set Default Sharing:**
   - You can set entire folders to "Anyone with link"
   - Then any file in that folder is automatically shareable

### **Storage Management:**

| Method | Storage Location | Capacity |
|--------|-----------------|----------|
| Local Upload | Server `/uploads/` | Limited by server disk |
| Google Drive | Your Google Account | 2TB (your plan) |
| External URL | External server | N/A |

**Recommendation:** Use Google Drive for your 2TB storage! 🎯

---

## 🔐 Security Considerations

### Google Drive Links:
- ✅ Only people with the link can access
- ✅ You can revoke access anytime
- ✅ Google handles all security
- ⚠️ Anyone with the link can view (don't share sensitive images publicly)

### Local Uploads:
- ✅ Stored on your server
- ✅ Controlled access through your backend
- ⚠️ Uses your server storage

---

## 🐛 Troubleshooting

### **"Image not loading" with Google Drive link:**

**Problem:** Link is not public

**Solution:**
1. Go to Google Drive
2. Right-click the file
3. Share → "Anyone with the link"
4. Try again

---

### **"File too large" error:**

**For local uploads:**
- Max size: 100MB
- **Solution:** Use Google Drive instead (supports much larger files!)

**For Google Drive:**
- No size limit from Ananta
- Limited only by your Google Drive storage

---

### **"Invalid file type":**

**Solution:**
- Only images and videos are supported
- Check file extension
- Convert to supported format (JPG, PNG, MP4, etc.)

---

## 📊 Comparison

| Feature | Local Upload | Google Drive | External URL |
|---------|-------------|--------------|--------------|
| Setup Required | ❌ None | ✅ Share link | ❌ None |
| Storage Used | 💾 Your Server | ☁️ Google (2TB) | 🌐 External |
| File Size Limit | 100MB | Your Google limit | N/A |
| Speed | 🚀 Fastest | ⚡ Fast | 🔄 Varies |
| Best For | Small files | Large files, videos | Public images |

---

## 🎯 Recommended Workflow

### **For Regular Use:**
1. Use **Local Upload** for quick posts with small images
2. Use **Google Drive** for videos and high-quality images
3. Use **External URLs** for sharing web images

### **For Maximum Storage:**
1. Upload all content to Google Drive first
2. Organize in folders
3. Share links from Drive to Ananta
4. You get 2TB of free storage! 🎉

---

## 📝 Example URLs

### Google Drive (automatically converted):
```
Input:  https://drive.google.com/file/d/1ABC...XYZ/view?usp=sharing
Output: https://drive.google.com/uc?export=view&id=1ABC...XYZ
```

### Local Upload:
```
/uploads/550e8400-e29b-41d4-a716-446655440000.jpg
```

### External URL:
```
https://example.com/image.jpg
```

---

## 🎉 You're All Set!

Now you can use your 2TB Google Drive storage for your social media app!

**Quick Start:**
1. Upload image to Google Drive
2. Get sharing link
3. Paste in Ananta
4. Post! 🚀

Enjoy unlimited storage! 📁✨
