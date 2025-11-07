package org.karna.ankur.ananta.service;

import lombok.RequiredArgsConstructor;
import org.karna.ankur.ananta.dto.LikeResponse;
import org.karna.ankur.ananta.entity.Like;
import org.karna.ankur.ananta.entity.Post;
import org.karna.ankur.ananta.entity.User;
import org.karna.ankur.ananta.exception.AlreadyExistsException;
import org.karna.ankur.ananta.exception.ResourceNotFoundException;
import org.karna.ankur.ananta.repository.LikeRepository;
import org.karna.ankur.ananta.repository.PostRepository;
import org.karna.ankur.ananta.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public LikeResponse likePost(UUID postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (likeRepository.existsByPostAndUser(post, user)) {
            throw new AlreadyExistsException("You have already liked this post");
        }

        Like like = Like.builder()
                .post(post)
                .user(user)
                .build();

        likeRepository.save(like);

        long likeCount = likeRepository.countByPost(post);
        return LikeResponse.builder()
                .liked(true)
                .likeCount(likeCount)
                .build();
    }

    @Transactional
    public LikeResponse unlikePost(UUID postId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Like like = likeRepository.findByPostAndUser(post, user)
                .orElseThrow(() -> new ResourceNotFoundException("Like not found"));

        likeRepository.delete(like);

        long likeCount = likeRepository.countByPost(post);
        return LikeResponse.builder()
                .liked(false)
                .likeCount(likeCount)
                .build();
    }

    @Transactional(readOnly = true)
    public long getLikeCount(UUID postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return likeRepository.countByPost(post);
    }
}
