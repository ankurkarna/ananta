package org.karna.ankur.ananta.controller;

import lombok.RequiredArgsConstructor;
import org.karna.ankur.ananta.dto.FollowResponse;
import org.karna.ankur.ananta.dto.UserResponse;
import org.karna.ankur.ananta.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<FollowResponse> followUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        String username = authentication.getName();
        FollowResponse response = followService.followUser(userId, username);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<FollowResponse> unfollowUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        String username = authentication.getName();
        FollowResponse response = followService.unfollowUser(userId, username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/followers")
    public ResponseEntity<List<UserResponse>> getFollowers(@PathVariable UUID userId) {
        List<UserResponse> followers = followService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/user/{userId}/following")
    public ResponseEntity<List<UserResponse>> getFollowing(@PathVariable UUID userId) {
        List<UserResponse> following = followService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/user/{userId}/followers/count")
    public ResponseEntity<Long> getFollowerCount(@PathVariable UUID userId) {
        long count = followService.getFollowerCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/user/{userId}/following/count")
    public ResponseEntity<Long> getFollowingCount(@PathVariable UUID userId) {
        long count = followService.getFollowingCount(userId);
        return ResponseEntity.ok(count);
    }
}
