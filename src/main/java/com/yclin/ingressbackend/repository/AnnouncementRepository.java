package com.yclin.ingressbackend.repository;

import com.yclin.ingressbackend.entity.domain.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}