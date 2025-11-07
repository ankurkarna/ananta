package org.karna.ankur.ananta.controller;

import lombok.RequiredArgsConstructor;
import org.karna.ankur.ananta.dto.LikeResponse;
import org.karna.ankur.ananta.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/post/{postId}")
    public ResponseEntity<LikeResponse> likePost(
            @PathVariable UUID postId,
            Authentication authentication) {
        String username = authentication.getName();
        LikeResponse response = likeService.likePost(postId, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<LikeResponse> unlikePost(
            @PathVariable UUID postId,
            Authentication authentication) {
        String username = authentication.getName();
        LikeResponse response = likeService.unlikePost(postId, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/post/{postId}/count")
    public ResponseEntity<Long> getLikeCount(@PathVariable UUID postId) {
        long count = likeService.getLikeCount(postId);
        return ResponseEntity.ok(count);
    }
}
