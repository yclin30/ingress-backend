package com.yclin.ingressbackend.entity.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resonators", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"location_id", "slot_number"})
})
public class Resonator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer level;

    @Column(nullable = false)
    private Integer health;

    @Column(name = "slot_number", nullable = false)
    private Integer slotNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployer_user_id", nullable = false)
    private User deployer;

    @Column(name = "deployed_at", updatable = false)
    private Instant deployedAt;

    @PrePersist
    protected void onCreate() {
        if (deployedAt == null) {
            deployedAt = Instant.now();
        }
        if (health == null) {
            health = 100; // 部署时默认为满健康度
        }
    }
}