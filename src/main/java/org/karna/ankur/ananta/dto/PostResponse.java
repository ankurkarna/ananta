package org.karna.ankur.ananta.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private UUID postId;
    private String imageUrl;
    private String caption;
    private UserResponse user;
    private long likeCount;
    private long commentCount;
    private boolean likedByCurrentUser;
    private LocalDateTime createdAt;
}
