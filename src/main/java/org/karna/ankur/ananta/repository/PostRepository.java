package org.karna.ankur.ananta.repository;

import org.karna.ankur.ananta.entity.Post;
import org.karna.ankur.ananta.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByUserOrderByCreatedAtDesc(User user);
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByUser_UserIDOrderByCreatedAtDesc(UUID userId);
}
