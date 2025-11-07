package org.karna.ankur.ananta.repository;

import org.karna.ankur.ananta.entity.Like;
import org.karna.ankur.ananta.entity.Post;
import org.karna.ankur.ananta.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    Optional<Like> findByPostAndUser(Post post, User user);
    boolean existsByPostAndUser(Post post, User user);
    long countByPost(Post post);
    List<Like> findByPost(Post post);
    void deleteByPostAndUser(Post post, User user);
}
