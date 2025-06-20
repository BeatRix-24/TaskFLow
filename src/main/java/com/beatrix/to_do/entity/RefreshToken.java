package com.beatrix.to_do.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String token;

    @ManyToOne
    private User user;

    private String deviceInfo;
    private String ipAddress;

    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    private boolean revoked;
}
