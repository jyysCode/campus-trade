
package com.campus.trade.controller;

import com.campus.trade.pojo.Message;
import com.campus.trade.pojo.Result;
import com.campus.trade.pojo.dto.MessageDTO;
import com.campus.trade.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    
    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    
    public MessageController(MessageService messageService, SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }
    
    @PostMapping("/send")
    public Result<String> send(@Valid @RequestBody MessageDTO messageDTO, @RequestAttribute("userId") Long userId) {
        messageService.send(messageDTO, userId);
        
        Map<String, Object> msg = new HashMap<>();
        msg.put("fromUserId", userId);
        msg.put("content", messageDTO.getContent());
        msg.put("productId", messageDTO.getProductId());
        
        messagingTemplate.convertAndSend("/queue/user/" + messageDTO.getToUserId(), msg);
        
        return Result.success("发送成功");
    }
    
    @GetMapping("/conversation/{otherUserId}")
    public Result<List<Message>> conversation(@PathVariable Long otherUserId, @RequestAttribute("userId") Long userId) {
        List<Message> messages = messageService.getConversation(userId, otherUserId);
        messageService.markAsRead(userId);
        return Result.success(messages);
    }

    @GetMapping("/my-conversations")
    public Result<List<Map<String, Object>>> myConversations(@RequestAttribute("userId") Long userId) {
        List<Map<String, Object>> conversations = messageService.getMyConversations(userId);
        return Result.success(conversations);
    }

    @GetMapping("/unread-count")
    public Result<Integer> unreadCount(@RequestAttribute("userId") Long userId) {
        int count = messageService.getUnreadCount(userId);
        return Result.success(count);
    }
    
    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Map<String, Object> handleChat(@RequestBody Map<String, Object> payload) {
        return payload;
    }
}
