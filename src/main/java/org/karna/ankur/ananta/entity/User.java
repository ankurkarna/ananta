package org.karna.ankur.ananta.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID userID = UUID.randomUUID();
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    private String bio;
    private String profileImageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String role;

}
