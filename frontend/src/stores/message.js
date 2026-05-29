import { defineStore } from 'pinia'
import axios from '../utils/axios'

export const useMessageStore = defineStore('message', {
  state: () => ({
    messages: [],
    conversations: [],
    unreadCount: 0
  }),
  actions: {
    async send(messageDTO) {
      await axios.post('/message/send', messageDTO)
    },
    async getConversation(otherUserId) {
      const data = await axios.get(`/message/conversation/${otherUserId}`)
      this.messages = Array.isArray(data) ? data : []
      return this.messages
    },
    async getMyConversations() {
      const data = await axios.get('/message/my-conversations')
      this.conversations = Array.isArray(data) ? data : []
      return this.conversations
    },
    async getUnreadCount() {
      const data = await axios.get('/message/unread-count')
      this.unreadCount = typeof data === 'number' ? data : 0
      return this.unreadCount
    }
  }
})
