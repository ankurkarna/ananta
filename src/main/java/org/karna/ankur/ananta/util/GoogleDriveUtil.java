package org.karna.ankur.ananta.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GoogleDriveUtil {

    /**
     * Converts a Google Drive sharing link to a direct image URL
     *
     * Input: https://drive.google.com/file/d/FILE_ID/view?usp=sharing
     * Output: https://drive.google.com/uc?export=view&id=FILE_ID
     *
     * @param driveLink Google Drive sharing link
     * @return Direct image URL
     */
    public String convertToDirectLink(String driveLink) {
        if (driveLink == null || driveLink.isEmpty()) {
            return driveLink;
        }

        // Pattern to extract file ID from Google Drive link
        Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(driveLink);

        if (matcher.find()) {
            String fileId = matcher.group(1);
            return "https://drive.google.com/uc?export=view&id=" + fileId;
        }

        // If already a direct link or not a Google Drive link, return as is
        return driveLink;
    }

    /**
     * Checks if a URL is a Google Drive link
     */
    public boolean isGoogleDriveLink(String url) {
        return url != null && url.contains("drive.google.com");
    }

    /**
     * Extracts file ID from Google Drive URL
     */
    public String extractFileId(String driveLink) {
        if (driveLink == null || driveLink.isEmpty()) {
            return null;
        }

        Pattern pattern = Pattern.compile("/d/([a-zA-Z0-9_-]+)");
        Matcher matcher = pattern.matcher(driveLink);

        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
