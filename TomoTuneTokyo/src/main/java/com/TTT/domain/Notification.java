package com.TTT.domain;

import jakarta.persistence.*;
import lombok.*;        // Lombok import 한 번에
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    @Column(name="user_id", nullable=false)
    private String userId;

    @Column(nullable=false)
    private String category;

    @Lob
    @Column(nullable=false)
    private String message;

    private String url;

    @Builder.Default
    @Column(name="is_read", nullable=false)
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name="created_at", updatable=false, nullable=false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt;
}