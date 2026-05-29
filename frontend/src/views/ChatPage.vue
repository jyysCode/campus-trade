<template>
  <div class="chat-page">
    <h1>💬 私信聊天</h1>
    <div class="chat-box" v-loading="loading">
      <div class="chat-messages" ref="msgContainer">
        <div v-for="msg in messages" :key="msg.id" :class="['msg-item', msg.fromUserId === userId ? 'msg-mine' : 'msg-other']">
          <div class="msg-bubble">{{ msg.content }}</div>
          <div class="msg-time">{{ msg.createTime }}</div>
        </div>
        <el-empty v-if="!loading && messages.length === 0" description="暂无消息，发送第一条吧" />
      </div>
      <div class="chat-input">
        <el-input v-model="content" placeholder="输入消息..." @keyup.enter="send" />
        <el-button type="primary" @click="send" :disabled="!content.trim()">发送</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/user'
import { getConversation, sendMessage } from '../api'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const userId = computed(() => userStore.userInfo?.id)
const otherUserId = computed(() => route.params.userId)
const messages = ref([])
const content = ref('')
const loading = ref(false)
const msgContainer = ref(null)

const loadMessages = async () => {
  if (!otherUserId.value) return
  loading.value = true
  try {
    const res = await getConversation(userId.value, otherUserId.value)
    messages.value = res.data.data || []
    await nextTick()
    if (msgContainer.value) msgContainer.value.scrollTop = msgContainer.value.scrollHeight
  } catch (e) {}
  loading.value = false
}

const send = async () => {
  if (!content.value.trim() || !otherUserId.value) return
  try {
    await sendMessage({ toUserId: otherUserId.value, content: content.value.trim() })
    content.value = ''
    loadMessages()
  } catch (e) {
    ElMessage.error('发送失败')
  }
}

onMounted(loadMessages)
</script>

<style scoped>
.chat-page { max-width: 700px; margin: 0 auto; padding: 20px; }
.chat-page h1 { margin-bottom: 20px; }
.chat-box { background: #fff; border-radius: 8px; overflow: hidden; }
.chat-messages { height: 400px; overflow-y: auto; padding: 16px; background: #f5f5f5; }
.msg-item { margin-bottom: 12px; }
.msg-mine { text-align: right; }
.msg-mine .msg-bubble { display: inline-block; background: #409eff; color: #fff; padding: 8px 14px; border-radius: 12px 12px 0 12px; max-width: 70%; text-align: left; }
.msg-other .msg-bubble { display: inline-block; background: #fff; padding: 8px 14px; border-radius: 12px 12px 12px 0; max-width: 70%; }
.msg-time { font-size: 11px; color: #999; margin-top: 4px; }
.chat-input { display: flex; gap: 8px; padding: 12px; border-top: 1px solid #eee; }
.chat-input .el-input { flex: 1; }
</style>
