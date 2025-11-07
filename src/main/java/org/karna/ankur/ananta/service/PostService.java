package org.karna.ankur.ananta.service;

import lombok.RequiredArgsConstructor;
import org.karna.ankur.ananta.dto.*;
import org.karna.ankur.ananta.entity.Post;
import org.karna.ankur.ananta.entity.User;
import org.karna.ankur.ananta.exception.ResourceNotFoundException;
import org.karna.ankur.ananta.exception.UnauthorizedException;
import org.karna.ankur.ananta.repository.CommentRepository;
import org.karna.ankur.ananta.repository.LikeRepository;
import org.karna.ankur.ananta.repository.PostRepository;
import org.karna.ankur.ananta.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponse createPost(CreatePostRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = Post.builder()
                .imageUrl(request.getImageUrl())
                .caption(request.getCaption())
                .user(user)
                .build();

        Post savedPost = postRepository.save(post);
        return mapToPostResponse(savedPost, user);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Post> posts = postRepository.findAllByOrderByCreatedAtDesc();
        return posts.stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID postId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return mapToPostResponse(post, currentUser);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUser(UUID userId, String currentUsername) {
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Post> posts = postRepository.findByUser_UserIDOrderByCreatedAtDesc(userId);
        return posts.stream()
                .map(post -> mapToPostResponse(post, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional
    public void deletePost(UUID postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUser().getUserID().equals(user.getUserID())) {
            throw new UnauthorizedException("You are not authorized to delete this post");
        }

        postRepository.delete(post);
    }

    private PostResponse mapToPostResponse(Post post, User currentUser) {
        long likeCount = likeRepository.countByPost(post);
        long commentCount = commentRepository.countByPost(post);
        boolean likedByCurrentUser = likeRepository.existsByPostAndUser(post, currentUser);

        return PostResponse.builder()
                .postId(post.getPostId())
                .imageUrl(post.getImageUrl())
                .caption(post.getCaption())
                .user(mapToUserResponse(post.getUser()))
                .likeCount(likeCount)
                .commentCount(commentCount)
                .likedByCurrentUser(likedByCurrentUser)
                .createdAt(post.getCreatedAt())
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserID())
                .username(user.getUsername())
                .email(user.getEmail())
                .bio(user.getBio())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
