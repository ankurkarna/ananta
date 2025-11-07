package org.karna.ankur.ananta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private UUID userId;
    private String username;
    private String email;
    private String bio;
    private String profileImageUrl;
    private long followerCount;
    private long followingCount;
    private long postCount;
    private boolean followedByCurrentUser;
}
