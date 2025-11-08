package org.karna.ankur.ananta.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.karna.ankur.ananta.dto.CreatePostRequest;
import org.karna.ankur.ananta.dto.PostResponse;
import org.karna.ankur.ananta.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        PostResponse response = postService.createPost(request, username);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(Authentication authentication) {
        String username = authentication.getName();
        List<PostResponse> posts = postService.getAllPosts(username);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/following")
    public ResponseEntity<List<PostResponse>> getFollowingFeed(Authentication authentication) {
        String username = authentication.getName();
        List<PostResponse> posts = postService.getFollowingFeed(username);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPostById(
            @PathVariable UUID postId,
            Authentication authentication) {
        String username = authentication.getName();
        PostResponse post = postService.getPostById(postId, username);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponse>> getPostsByUser(
            @PathVariable UUID userId,
            Authentication authentication) {
        String username = authentication.getName();
        List<PostResponse> posts = postService.getPostsByUser(userId, username);
        return ResponseEntity.ok(posts);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable UUID postId,
            Authentication authentication) {
        String username = authentication.getName();
        postService.deletePost(postId, username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable UUID postId,
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        PostResponse response = postService.updatePost(postId, request, username);
        return ResponseEntity.ok(response);
    }
}
