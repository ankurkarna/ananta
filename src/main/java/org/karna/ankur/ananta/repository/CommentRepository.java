package org.karna.ankur.ananta.repository;

import org.karna.ankur.ananta.entity.Comment;
import org.karna.ankur.ananta.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);
    List<Comment> findByPost_PostIdOrderByCreatedAtDesc(UUID postId);
    long countByPost(Post post);
}
