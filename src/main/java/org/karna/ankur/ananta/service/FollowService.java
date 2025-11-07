package org.karna.ankur.ananta.service;

import lombok.RequiredArgsConstructor;
import org.karna.ankur.ananta.dto.FollowResponse;
import org.karna.ankur.ananta.dto.UserResponse;
import org.karna.ankur.ananta.entity.Follow;
import org.karna.ankur.ananta.entity.User;
import org.karna.ankur.ananta.exception.AlreadyExistsException;
import org.karna.ankur.ananta.exception.ResourceNotFoundException;
import org.karna.ankur.ananta.repository.FollowRepository;
import org.karna.ankur.ananta.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public FollowResponse followUser(UUID followingId, String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User to follow not found"));

        if (follower.getUserID().equals(following.getUserID())) {
            throw new AlreadyExistsException("You cannot follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new AlreadyExistsException("You are already following this user");
        }

        Follow follow = Follow.builder()
                .follower(follower)
                .following(following)
                .build();

        followRepository.save(follow);

        return FollowResponse.builder()
                .following(true)
                .followerCount(followRepository.countByFollowing(following))
                .followingCount(followRepository.countByFollower(follower))
                .build();
    }

    @Transactional
    public FollowResponse unfollowUser(UUID followingId, String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User to unfollow not found"));

        Follow follow = followRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new ResourceNotFoundException("Follow relationship not found"));

        followRepository.delete(follow);

        return FollowResponse.builder()
                .following(false)
                .followerCount(followRepository.countByFollowing(following))
                .followingCount(followRepository.countByFollower(follower))
                .build();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getFollowers(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Follow> follows = followRepository.findByFollowing(user);
        return follows.stream()
                .map(follow -> mapToUserResponse(follow.getFollower()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getFollowing(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Follow> follows = followRepository.findByFollower(user);
        return follows.stream()
                .map(follow -> mapToUserResponse(follow.getFollowing()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getFollowerCount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return followRepository.countByFollowing(user);
    }

    @Transactional(readOnly = true)
    public long getFollowingCount(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return followRepository.countByFollower(user);
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
