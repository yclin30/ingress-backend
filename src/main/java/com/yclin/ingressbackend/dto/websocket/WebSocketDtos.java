package com.yclin.ingressbackend.dto.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

public class WebSocketDtos {

    // --- 客户端发送到 /app/chat.sendMessage 的消息体 ---
    @Getter
    @Setter
    public static class ChatMessageRequest {
        private String content;
    }

    // --- 服务器广播到 /topic/public 或 /topic/faction/{id} 的消息体 ---
    @Getter
    @Setter
    @AllArgsConstructor
    public static class ChatMessageBroadcast {
        private String sender;
        private String content;
        private final String type = "CHAT"; // 类型固定为 CHAT
    }

    // --- 服务器广播到 /topic/announcements 的消息体 ---
    @Getter
    @Setter
    @AllArgsConstructor
    public static class AnnouncementBroadcast {
        private String content;
        private Instant timestamp;
    }
}