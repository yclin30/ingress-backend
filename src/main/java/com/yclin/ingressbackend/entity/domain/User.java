package com.yclin.ingressbackend.entity.domain;

import com.yclin.ingressbackend.entity.enums.UserGender;
import com.yclin.ingressbackend.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.*; // <-- 修改 imports

import java.time.Instant;
import java.util.Objects; // <-- 新增 import

// 1. 移除 @Data，使用更精确的注解
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    // ... (所有其他字段保持完全不变) ...
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    private UserGender gender;

    @Column(length = 255)
    private String introduction;

    @Column(nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer level;

    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long experience;

    @Column(name = "is_banned", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isBanned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faction_id")
    private Faction faction;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // ... (@PrePersist 方法保持不变) ...
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    // --- 明确的 Getter (这个不再需要了，因为 @Getter 会正确处理) ---
    // public Boolean getIsBanned() { return this.isBanned; }

    // 2. 手动实现 equals 和 hashCode，只基于 ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        // 只有在 id 不为 null 的情况下才比较 id，否则认为它们不相等
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        // 使用一个固定的类哈希值，确保即使在 id 为 null 时（新实体）也有一个稳定的哈希码
        return getClass().hashCode();
    }
}