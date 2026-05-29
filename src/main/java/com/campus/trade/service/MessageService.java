package com.campus.trade.service;

import com.campus.trade.pojo.Message;
import com.campus.trade.pojo.dto.MessageDTO;

import java.util.List;
import java.util.Map;

public interface MessageService {

    void send(MessageDTO messageDTO, Long userId);

    List<Message> getConversation(Long userId, Long otherUserId);

    void markAsRead(Long userId);

    List<Map<String, Object>> getMyConversations(Long userId);

    int getUnreadCount(Long userId);
}