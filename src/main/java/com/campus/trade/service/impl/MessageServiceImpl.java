package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.campus.trade.exception.BusinessException;
import com.campus.trade.mapper.MessageMapper;
import com.campus.trade.mapper.UserMapper;
import com.campus.trade.pojo.Message;
import com.campus.trade.pojo.User;
import com.campus.trade.pojo.dto.MessageDTO;
import com.campus.trade.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageMapper messageMapper;
    private final UserMapper userMapper;

    public MessageServiceImpl(MessageMapper messageMapper, UserMapper userMapper) {
        this.messageMapper = messageMapper;
        this.userMapper = userMapper;
    }

    @Override
    public void send(MessageDTO messageDTO, Long userId) {
        if (userId.equals(messageDTO.getToUserId())) {
            throw new BusinessException("不能给自己发送消息");
        }
        Message message = new Message();
        message.setFromUserId(userId);
        message.setToUserId(messageDTO.getToUserId());
        message.setContent(messageDTO.getContent());
        message.setProductId(messageDTO.getProductId());
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
        log.info("消息发送成功, fromUserId={}, toUserId={}", userId, messageDTO.getToUserId());
    }

    @Override
    public List<Message> getConversation(Long userId, Long otherUserId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.and(w -> w.eq("from_user_id", userId).eq("to_user_id", otherUserId))
               .or(w -> w.eq("from_user_id", otherUserId).eq("to_user_id", userId));
        wrapper.orderByAsc("create_time");
        return messageMapper.selectList(wrapper);
    }

    @Override
    public void markAsRead(Long userId) {
        UpdateWrapper<Message> wrapper = new UpdateWrapper<>();
        wrapper.eq("to_user_id", userId);
        wrapper.eq("is_read", 0);
        wrapper.set("is_read", 1);
        messageMapper.update(null, wrapper);
    }

    @Override
    public List<Map<String, Object>> getMyConversations(Long userId) {
        log.info("获取用户会话列表, userId={}", userId);
        List<Map<String, Object>> conversations = new ArrayList<>();

        if (userId == null) {
            return conversations;
        }

        // 查询发送的消息
        QueryWrapper<Message> sendWrapper = new QueryWrapper<>();
        sendWrapper.eq("from_user_id", userId);
        sendWrapper.orderByDesc("create_time");
        List<Message> sendMessages = messageMapper.selectList(sendWrapper);
        log.info("发送的消息数量: {}", sendMessages.size());

        // 查询接收的消息
        QueryWrapper<Message> receiveWrapper = new QueryWrapper<>();
        receiveWrapper.eq("to_user_id", userId);
        receiveWrapper.orderByDesc("create_time");
        List<Message> receiveMessages = messageMapper.selectList(receiveWrapper);
        log.info("接收的消息数量: {}", receiveMessages.size());

        // 用LinkedHashMap保持插入顺序，key=otherUserId, value=该会话最后一条消息
        Map<Long, Message> lastMsgMap = new LinkedHashMap<>();
        Map<Long, Long> unreadMap = new HashMap<>();

        // 处理发送的消息
        for (Message msg : sendMessages) {
            Long toId = msg.getToUserId();
            if (toId == null) continue;
            if (!lastMsgMap.containsKey(toId)) {
                lastMsgMap.put(toId, msg);
            }
        }

        // 处理接收的消息
        for (Message msg : receiveMessages) {
            Long fromId = msg.getFromUserId();
            if (fromId == null) continue;
            if (!lastMsgMap.containsKey(fromId)) {
                lastMsgMap.put(fromId, msg);
            }
            // 统计未读
            if (msg.getIsRead() != null && msg.getIsRead() == 0) {
                unreadMap.merge(fromId, 1L, Long::sum);
            }
        }

        log.info("会话分组数量: {}", lastMsgMap.size());

        // 构建会话列表
        for (Map.Entry<Long, Message> entry : lastMsgMap.entrySet()) {
            Long otherUserId = entry.getKey();
            Message lastMsg = entry.getValue();

            log.info("处理会话: otherUserId={}, lastMsg={}", otherUserId, lastMsg.getContent());

            User otherUser = userMapper.selectById(otherUserId);
            if (otherUser == null) {
                log.warn("未找到用户: {}（可能已删除），跳过该会话", otherUserId);
                continue;
            }

            String otherUserName = otherUser.getNickname() != null ? otherUser.getNickname() : otherUser.getUsername();
            log.info("找到用户: id={}, name={}", otherUserId, otherUserName);

            Map<String, Object> conv = new HashMap<>();
            conv.put("otherUserId", otherUserId);
            conv.put("otherUserName", otherUserName);
            conv.put("otherUserAvatar", otherUser.getAvatar());
            conv.put("lastMessage", lastMsg.getContent());
            conv.put("lastMessageTime", lastMsg.getCreateTime());
            conv.put("unreadCount", unreadMap.getOrDefault(otherUserId, 0L));
            conversations.add(conv);
            log.info("添加会话成功: {}", conv);
        }

        log.info("最终返回会话数量: {}", conversations.size());
        return conversations;
    }

    @Override
    public int getUnreadCount(Long userId) {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.eq("to_user_id", userId);
        wrapper.eq("is_read", 0);
        return messageMapper.selectCount(wrapper).intValue();
    }
}
