package com.yclin.ingressbackend.controller;

import com.yclin.ingressbackend.config.SecurityUser;
import com.yclin.ingressbackend.dto.websocket.WebSocketDtos.ChatMessageBroadcast;
import com.yclin.ingressbackend.dto.websocket.WebSocketDtos.ChatMessageRequest;
import com.yclin.ingressbackend.entity.domain.ChatMessage;
import com.yclin.ingressbackend.entity.domain.User;
import com.yclin.ingressbackend.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageRequest chatMessage, Authentication authentication) {
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        User sender = securityUser.getUser();

        // 1. 创建广播消息
        ChatMessageBroadcast broadcastMessage = new ChatMessageBroadcast(sender.getUsername(), chatMessage.getContent());

        // 2. 确定广播目的地
        String publicChannel = "/topic/public";
        
        // 3. 广播到公共频道
        messagingTemplate.convertAndSend(publicChannel, broadcastMessage);

        // 如果用户有阵营，则额外广播到阵营频道
        if (sender.getFaction() != null) {
            String factionChannel = "/topic/faction/" + sender.getFaction().getId();
            messagingTemplate.convertAndSend(factionChannel, broadcastMessage);
        }

        // 4. 将消息持久化到数据库
        ChatMessage messageToSave = ChatMessage.builder()
                .sender(sender)
                .senderUsername(sender.getUsername())
                .channel("public") // 简化处理，可根据业务扩展
                .content(chatMessage.getContent())
                .build();
        chatMessageRepository.save(messageToSave);
    }
}