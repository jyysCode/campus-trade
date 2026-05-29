<template>
  <div class="my-messages-page">
    <div class="messages-container">
      <h2 class="page-title">我的消息</h2>
      
      <div v-if="loading" class="loading-state">
        <el-skeleton :rows="5" animated />
      </div>
      
      <div v-else-if="conversations.length === 0" class="empty-state">
        <el-icon :size="64" class="empty-icon"><ChatDotRound /></el-icon>
        <p>暂无消息</p>
        <el-button type="primary" @click="$router.push('/')">去逛逛</el-button>
      </div>
      
      <div v-else class="conversation-list">
        <div 
          v-for="conv in conversations" 
          :key="conv.otherUserId"
          class="conversation-item"
          @click="goToChat(conv.otherUserId)"
        >
          <div class="avatar-wrapper">
            <img :src="getUserAvatar(conv.otherUserId, conv.otherUserName)" class="user-avatar" />
            <span v-if="conv.unreadCount > 0" class="unread-badge">{{ conv.unreadCount }}</span>
          </div>
          <div class="conversation-content">
            <div class="conversation-header">
              <span class="user-name">{{ conv.otherUserName }}</span>
              <span class="message-time">{{ formatTime(conv.lastMessageTime) }}</span>
            </div>
            <p class="last-message">{{ conv.lastMessage }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useMessageStore } from '../stores/message'
import { ChatDotRound } from '@element-plus/icons-vue'
import { getUserAvatar } from '../utils/image'
import { ElMessage } from 'element-plus'

const router = useRouter()
const messageStore = useMessageStore()

const conversations = ref([])
const loading = ref(false)

const loadConversations = async () => {
  loading.value = true
  try {
    const data = await messageStore.getMyConversations()
    console.log('获取到会话数据:', data)
    // 确保数据是数组，并过滤掉无效的会话
    if (Array.isArray(data)) {
      conversations.value = data.filter(conv => {
        const isValid = conv && conv.otherUserId && conv.otherUserName
        if (!isValid) {
          console.warn('过滤掉无效会话:', conv)
        }
        return isValid
      })
    } else {
      console.error('返回数据不是数组:', data)
      conversations.value = []
    }
    console.log('处理后会话数量:', conversations.value.length)
  } catch (e) {
    console.error('加载消息列表失败', e)
    ElMessage.error('加载消息列表失败')
  } finally {
    loading.value = false
  }
}

const goToChat = (otherUserId) => {
  router.push(`/chat/${otherUserId}`)
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  // 小于1小时显示分钟前
  if (diff < 3600000) {
    const minutes = Math.floor(diff / 60000)
    return minutes < 1 ? '刚刚' : `${minutes}分钟前`
  }
  // 小于24小时显示小时前
  if (diff < 86400000) {
    return `${Math.floor(diff / 3600000)}小时前`
  }
  // 小于7天显示几天前
  if (diff < 604800000) {
    return `${Math.floor(diff / 86400000)}天前`
  }
  // 显示日期
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}

onMounted(() => {
  loadConversations()
})
</script>

<style scoped>
.my-messages-page {
  min-height: calc(100vh - 60px);
  background: #f5f5f5;
  padding: 20px;
}

.messages-container {
  max-width: 800px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.loading-state {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: #fff;
  border-radius: 12px;
}

.empty-icon {
  color: #ddd;
  margin-bottom: 16px;
}

.empty-state p {
  font-size: 16px;
  color: #999;
  margin-bottom: 24px;
}

.conversation-list {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  transition: background 0.2s;
  border-bottom: 1px solid #f5f5f5;
}

.conversation-item:last-child {
  border-bottom: none;
}

.conversation-item:hover {
  background: #f9f9f9;
}

.avatar-wrapper {
  position: relative;
  margin-right: 16px;
}

.user-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
}

.unread-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #f56c6c;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  text-align: center;
}

.conversation-content {
  flex: 1;
  min-width: 0;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.user-name {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.last-message {
  font-size: 14px;
  color: #666;
  margin: 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>
