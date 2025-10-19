package com.yclin.ingressbackend.repository;

import com.yclin.ingressbackend.entity.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
}