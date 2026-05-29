<template>
  <div class="chat-page">
    <div class="chat-content">
      <div class="chat-sidebar" v-if="showSidebar">
        <div class="sidebar-header">
          <h3>🤖 AI 助手</h3>
          <span class="sidebar-close" @click="showSidebar = false">✕</span>
        </div>
        <div class="sidebar-body">
          <p class="ai-hint">🤖 AI 智能助手 - 模拟讨价还价场景</p>
          <div class="ai-desc">点击下方按钮，AI将帮你生成讨价还价话术，并模拟卖家回复</div>
          <div class="ai-actions">
            <button 
              v-for="action in aiActions" 
              :key="action.key" 
              class="ai-action-btn"
              @click="triggerAI(action.key)"
            >
              {{ action.label }}
            </button>
          </div>
          <div v-if="aiMessages.length > 0" class="ai-previews">
            <div class="ai-preview-title">📝 话术预览</div>
            <div v-for="(aiMsg, idx) in aiMessages" :key="idx" :class="['ai-preview-item', aiMsg.isMy ? 'buyer-preview' : 'seller-preview']">
              <span class="ai-role">{{ aiMsg.isMy ? '🧑 你（买家）' : '👨‍💼 卖家回复' }}</span>
              <span class="ai-content">{{ aiMsg.content }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="chat-main" :class="{ 'sidebar-open': showSidebar }">
        <div class="chat-header">
          <button class="back-btn" @click="goBack">← 返回</button>
          <div class="chat-user-info">
            <img :src="otherUserAvatar" alt="头像" class="chat-avatar" />
            <div>
              <span class="chat-username">{{ otherUserName }}</span>
              <span class="chat-status">在线</span>
            </div>
          </div>
          <button class="ai-toggle" :class="{ active: aiEnabled }" @click="toggleAI">
            🤖 {{ aiEnabled ? '关闭AI' : 'AI助手' }}
          </button>
        </div>

        <div class="chat-messages" ref="messagesContainer">
          <div v-if="messages.length === 0" class="chat-empty">
            <span>💬</span>
            <p>开始对话吧</p>
            <p class="chat-tip">点击右侧 AI 助手可以模拟讨价还价</p>
          </div>
          <div v-for="msg in messages" :key="msg.id" :class="['message-item', msg.fromUserId === currentUserId ? 'sent' : 'received']">
            <div class="message-bubble">
              <p>{{ msg.content }}</p>
            </div>
            <span class="message-time">{{ formatTime(msg.createTime) }}</span>
          </div>
          <div v-if="isAITyping" class="message-item received">
            <div class="message-bubble typing">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
          </div>
        </div>

        <div class="chat-input-area">
          <div class="quick-replies" v-if="quickReplies.length > 0">
            <button v-for="(qr, idx) in quickReplies" :key="idx" class="quick-reply-btn" @click="sendQuickReply(qr)">{{ qr }}</button>
          </div>
          <div class="input-row">
            <input v-model="inputMessage" class="chat-input" placeholder="输入消息..." @keyup.enter="sendMessage" />
            <button class="send-btn" @click="sendMessage" :disabled="!inputMessage.trim()">发送</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useMessageStore } from '../stores/message'
import { useUserStore } from '../stores/user'
import { useProductStore } from '../stores/product'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const messageStore = useMessageStore()
const userStore = useUserStore()
const productStore = useProductStore()

const messages = ref([])
const inputMessage = ref('')
const messagesContainer = ref(null)
const currentUserId = ref(null)
const otherUserId = ref(null)
const otherUserName = ref('用户')
const otherUserAvatar = ref('https://picsum.photos/seed/other/40/40')
const showSidebar = ref(false)
const aiEnabled = ref(false)  // AI助手是否开启
const isAITyping = ref(false)
const aiMessages = ref([])
const quickReplies = ref([])

// 商品上下文信息
const productContext = ref(null)

const aiActions = [
  { key: 'offer-low', label: '💸 出低价试探' },
  { key: 'counter-offer', label: '💰 卖家还价' },
  { key: 'accept-deal', label: '🤝 达成交易' },
  { key: 'negotiate', label: '🗣️ 协商价格' },
  { key: 'ask-condition', label: '📋 询问成色' }
]

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const goBack = () => { router.back() }

// 切换AI助手开关
const toggleAI = () => {
  aiEnabled.value = !aiEnabled.value
  showSidebar.value = aiEnabled.value  // 开启时自动展开侧边栏
  if (aiEnabled.value) {
    ElMessage.success('AI助手已开启，发送消息后AI将自动模拟卖家回复')
  } else {
    showSidebar.value = false
    ElMessage.info('AI助手已关闭')
  }
}

// ========================================
// AI智能回复引擎 v2 - 基于商品上下文的智能对话
// ========================================
const randomPick = (arr) => arr[Math.floor(Math.random() * arr.length)]

// 从商品名称/描述中提取关键属性
const extractProductAttributes = (product) => {
  if (!product) return { type: '通用', name: '这个商品', usageTime: '几个月', condition: '九成新' }
  
  const name = product.name || '这个商品'
  const desc = product.description || ''
  const price = product.price || 0
  const combined = (name + ' ' + desc).toLowerCase()
  
  // 判断商品类型
  let type = '通用'
  let usageDesc = '用了几个月'
  let conditionDesc = '九成新，功能正常'
  let hasBox = '有原装包装盒'
  
  // 电子产品
  if (/手机|iphone|华为|小米|oppo|vivo|三星|平板|ipad|电脑|笔记本|macbook/.test(combined)) {
    type = '手机数码'
    usageDesc = '用了大概半年，一直贴膜带壳'
    conditionDesc = '屏幕无划痕，电池健康度90%以上'
    hasBox = '有原装充电器和包装盒'
  } else if (/耳机|airpods|蓝牙|音箱|音响/.test(combined)) {
    type = '耳机音响'
    usageDesc = '用了两三个月，音质很好'
    conditionDesc = '没有磕碰，续航正常'
    hasBox = '有充电盒和原装线'
  } else if (/电动牙刷|剃须刀|吹风机|美容仪|电风扇|加湿器/.test(combined)) {
    type = '小家电'
    usageDesc = '买来用了三四个月，很新'
    conditionDesc = '机身干净无污渍，功能一切正常'
    hasBox = '有原装充电底座和刷头'
  } else if (/键盘|鼠标|显示器|摄像头|路由器|硬盘|u盘/.test(combined)) {
    type = '电脑外设'
    usageDesc = '用了几个月，手感很好'
    conditionDesc = '按键无磨损，响应灵敏'
    hasBox = '有原装包装'
  } else if (/书|教材|课本|考研|四六级|公务员/.test(combined)) {
    type = '书籍教材'
    usageDesc = '翻过几遍，有少量笔记标注'
    conditionDesc = '书页完整，没有缺页'
    hasBox = '无包装'
  } else if (/衣服|裤子|鞋|外套|裙子|卫衣|T恤|包包|背包/.test(combined)) {
    type = '服饰鞋包'
    usageDesc = '穿过几次，洗过很干净'
    conditionDesc = '没有破损和明显污渍'
    hasBox = '无原包装'
  } else if (/自行车|电动车|滑板|球拍|篮球|足球|瑜伽垫/.test(combined)) {
    type = '运动器材'
    usageDesc = '用过一段时间，性能良好'
    conditionDesc = '各部件正常，没有损坏'
    hasBox = '无原包装'
  } else if (/化妆品|护肤|面膜|口红|精华|防晒/.test(combined)) {
    type = '美妆护肤'
    usageDesc = '用过几次，还剩大半'
    conditionDesc = '保存良好，在保质期内'
    hasBox = '有外包装'
  }
  
  return { type, name, price, usageDesc, conditionDesc, hasBox, description: desc }
}

const generateAIResponse = (userMessage) => {
  const msg = userMessage.trim()
  const p = extractProductAttributes(productContext.value)
  const price = p.price ? `¥${p.price}` : '页面标价'
  
  // 询问成色/使用情况
  if (/成色|新旧|磨损|瑕疵|划痕|磕碰|损坏|维修/.test(msg)) {
    return randomPick([
      `${p.name}的${p.conditionDesc}，${p.usageDesc}，整体很新的！`,
      `成色很好的，${p.conditionDesc}。${p.usageDesc}，基本跟新的差不多。`,
      `你可以看下图片，实物比照片还新。${p.conditionDesc}，没有任何质量问题。`
    ])
  }
  
  // 询问用了多久/什么时候买的
  if (/用了多久|什么时候买|多久了|买了多久|什么时候入手/.test(msg)) {
    return randomPick([
      `${p.usageDesc}，当时买来${p.type === '书籍教材' ? '学完就闲置了' : '用得不多，基本闲置了'}。`,
      `大概${p.usageDesc}吧，买来${p.type === '书籍教材' ? '翻了几遍就没怎么看了' : '新鲜感过了就闲置了'}，所以出掉。`,
      `${p.usageDesc}，成色还很新的，${p.type === '书籍教材' ? '笔记可以做参考' : '功能全都正常'}。`
    ])
  }
  
  // 询问当面看/交易
  if (/当面|面交|看货|线下|自提|见面/.test(msg)) {
    return randomPick([
      `可以的，我在学校这边，方便的话可以约时间当面看货。`,
      `没问题，当面交易最好了。你在哪个校区？我看看方不方便。`,
      `可以的，约个时间你来看看实物，满意再交易。`
    ])
  }
  
  // 询问配件/包装
  if (/配件|包装|盒子|充电器|刷头|原装|发票|保修/.test(msg)) {
    return randomPick([
      `${p.hasBox}，配件都是原装的。`,
      `有的，${p.hasBox}，配件齐全。`,
      `${p.hasBox}，买来的时候东西都保留着呢。`
    ])
  }
  
  // 砍价
  if (/便宜|少点|再少|砍价|优惠|折扣|能不能少|最低|能少/.test(msg)) {
    return randomPick([
      `亲，${price}已经是实价了，${p.name}的${p.conditionDesc}，这个价格真的很良心了 😅`,
      `这个价格已经很划算了，你看看同款全新的要多少钱。实在不好意思再少了 😢`,
      `这样吧，看你诚心要，${p.hasBox ? '我再送你个配件' : '我给你包邮'}，价格真的不能再少了～`
    ])
  }
  
  // 询问价格/多少钱
  if (/多少钱|什么价|价格|报价|几块|几块|什么价格/.test(msg)) {
    return randomPick([
      `价格就是${price}哦，${p.name}的${p.conditionDesc}，性价比很高的！`,
      `${price}，${p.usageDesc}，这个价格很实在了。`,
      `页面标的就是实价${price}，没有隐藏费用。`
    ])
  }
  
  // 询问发货/快递
  if (/发货|快递|邮费|包邮|运费|几天到/.test(msg)) {
    return randomPick([
      `同城的话可以当面交易，不需要快递。外地的话我给你发顺丰，运费到付。`,
      `拍下后我尽快安排发货，一般${p.type === '书籍教材' ? '中通/圆通' : '顺丰'}，${p.type === '小家电' ? '包装会仔细包好' : '不用担心损坏'}。`,
      `可以的，${p.type === '书籍教材' ? '发中通大概2-3天到' : '发顺丰次日达'}，包装我会弄好的。`
    ])
  }
  
  // 询问质量/正品
  if (/质量|正品|真假|好不好|怎么样|靠谱/.test(msg)) {
    return randomPick([
      `质量绝对没问题，${p.type === '书籍教材' ? '是正版书' : '可以当面验货的'}，有问题全额退款。`,
      `放心，${p.conditionDesc}。你可以当面看货，满意再付款。`,
      `质量很好的，${p.usageDesc}，${p.type === '书籍教材' ? '内容很新' : '功能完全正常'}。`
    ])
  }
  
  // 询问为什么出/原因
  if (/为什么卖|为什么出|原因|怎么不用了|换新/.test(msg)) {
    return randomPick([
      `${p.type === '书籍教材' ? '考完试了用不上了' : p.type === '小家电' ? '换了个新的，旧的闲置了' : '升级了，这个用不上了'}，所以出掉。`,
      `${p.type === '书籍教材' ? '课程结束了，书留着也没用' : '买来用了一段时间，现在不需要了'}，成色很好出给有需要的人。`,
      `毕业了/换新的了，${p.name}还很好，不想浪费就出掉了。`
    ])
  }
  
  // 询问能不能小刀/小刀
  if (/小刀|刀|微调/.test(msg)) {
    return randomPick([
      `亲，${price}已经是底价了，真的没法再少了 😅`,
      `不好意思，这个价格已经很实在了，不能再少了呢。`,
      `价格已经很优惠了，${p.hasBox ? '配件齐全' : '成色很好'}，这个价格很值的。`
    ])
  }
  
  // 确认购买
  if (/要了|买了|成交|行|可以|好的|拍下|付款/.test(msg)) {
    return randomPick([
      `好的！那你看怎么方便，当面交易还是快递？`,
      `没问题！你方便什么时候？当面还是快递？`,
      `好的亲！确认收货后记得给个好评哦～`
    ])
  }
  
  // 打招呼
  if (/在吗|在线|你好|hi|hello|嗨|哈喽/.test(msg)) {
    return randomPick([
      `在的！请问对${p.name}有什么想了解的吗？`,
      `你好！${p.name}还在的，有什么问题尽管问～`,
      `在的呢，欢迎咨询${p.name}的相关问题！`
    ])
  }
  
  // 询问是否还在
  if (/还在吗|出了没|还有吗|有没有|卖没卖/.test(msg)) {
    return randomPick([
      `还在的！${p.name}还没有出掉，你要的话可以聊聊～`,
      `还在呢，${p.conditionDesc}，随时可以交易。`,
      `有的，${p.name}还没出，你感兴趣吗？`
    ])
  }
  
  // 询问具体功能
  if (/功能|能用|正常|好用|效果/.test(msg)) {
    return randomPick([
      `功能一切正常的！${p.conditionDesc}，你可以当面测试。`,
      `当然能用，${p.usageDesc}，${p.type === '小家电' ? '续航也很好' : '各项功能都没问题'}。`,
      `完全正常，${p.usageDesc}，${p.type === '书籍教材' ? '内容很全' : '没有任何故障'}。`
    ])
  }
  
  // 默认回复
  return randomPick([
    `嗯嗯，关于${p.name}你还有什么想了解的吗？`,
    `好的呢，${p.name}${p.conditionDesc}，价格${price}，性价比很高的！`,
    `没问题，你还有什么问题吗？${p.name}随时可以交易～`
  ])
}

// AI回复用户消息
const replyAsAI = (userMessage) => {
  const aiReply = generateAIResponse(userMessage)
  
  isAITyping.value = true
  setTimeout(() => {
    isAITyping.value = false
    const aiReplyMsg = {
      id: Date.now() + 1,
      fromUserId: otherUserId.value,
      toUserId: currentUserId.value,
      content: `【AI卖家】${aiReply}`,
      createTime: new Date().toISOString()
    }
    messages.value.push(aiReplyMsg)
    scrollToBottom()
  }, 1000 + Math.random() * 1000)
}

const sendMessage = async () => {
  const content = inputMessage.value.trim()
  if (!content) return
  if (!otherUserId.value) {
    ElMessage.error('无法确定聊天对象')
    return
  }
  const msgContent = content
  inputMessage.value = ''
  try {
    await messageStore.send({
      toUserId: otherUserId.value,
      content: msgContent,
      productId: null
    })
    // 发送成功后立即在本地添加消息，避免等待加载
    messages.value.push({
      id: Date.now(),
      fromUserId: currentUserId.value,
      toUserId: otherUserId.value,
      content: msgContent,
      createTime: new Date().toISOString()
    })
    scrollToBottom()
    // 只有AI助手开启时才触发AI智能回复
    if (aiEnabled.value) {
      replyAsAI(msgContent)
    }
    // 后台刷新消息列表（静默失败）
    loadMessages().catch(() => {})
  } catch (e) {
    console.error('发送消息失败', e)
    ElMessage.error(e.message || '发送失败')
    inputMessage.value = msgContent // 恢复输入内容
  }
}

const sendQuickReply = (text) => {
  inputMessage.value = text
  sendMessage()
}

const loadMessages = async () => {
  try {
    messages.value = await messageStore.getConversation(otherUserId.value)
    scrollToBottom()
  } catch (e) {
    console.error('加载消息失败', e)
  }
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}

const triggerAI = async (actionKey) => {
  const responses = {
    'offer-low': {
      buyer: '老板，这个还能便宜点吗？我预算有限，能少点不？🥺',
      seller: '【AI模拟卖家回复】诚心买的话可以小刀，你说个价？',
      replies: ['最低多少？', '200可以吗？', '还在吗？']
    },
    'counter-offer': {
      buyer: '能不能再便宜50？学生党预算有限😅',
      seller: '【AI模拟卖家回复】行吧，看你诚心要，那就减50，爽快交易！',
      replies: ['好的，我要了！', '包邮吗？', '再便宜20吧']
    },
    'accept-deal': {
      buyer: '好的，就这个价吧，怎么交易？',
      seller: '【AI模拟卖家回复】OK，拍下我改价，今天就能发货！',
      replies: ['拍下了', '已付款', '什么时候发货？']
    },
    'negotiate': {
      buyer: '我看了下其他家，价格比你的便宜哎，能再优惠点吗？',
      seller: '【AI模拟卖家回复】我的成色比他们好，一分钱一分货嘛。这样吧，各让一步，再少30？',
      replies: ['好吧，成交', '送个配件吧', '我再想想']
    },
    'ask-condition': {
      buyer: '请问东西用了多久？有没有什么瑕疵？',
      seller: '【AI模拟卖家回复】用了三个月，一直戴壳贴膜，没有任何划痕，和新的一样！',
      replies: ['有发票吗？', '什么时候买的？', '可以当面看吗？']
    }
  }

  const scenario = responses[actionKey]
  if (!scenario) return

  // 显示AI模拟对话到侧边栏
  aiMessages.value = [
    { role: 'buyer', content: scenario.buyer, isMy: true },
    { role: 'seller', content: scenario.seller, isMy: false }
  ]
  quickReplies.value = scenario.replies
  
  // 自动发送买家消息到卖家
  await sendMessageDirect(scenario.buyer)
  
  // AI扮演卖家模拟回复（只显示，不发送）
  isAITyping.value = true
  setTimeout(() => {
    isAITyping.value = false
    // AI卖家回复只添加到本地显示，不发送到服务器
    const aiReplyMsg = {
      id: Date.now(),
      fromUserId: otherUserId.value, // 模拟卖家发送
      toUserId: currentUserId.value,
      content: scenario.seller,
      createTime: new Date().toISOString()
    }
    messages.value.push(aiReplyMsg)
    scrollToBottom()
    ElMessage.success('AI模拟卖家回复已生成')
  }, 1500)
}

// 直接发送消息到服务器（不触发AI回复）
const sendMessageDirect = async (content) => {
  if (!otherUserId.value) {
    ElMessage.error('无法确定聊天对象')
    return
  }
  try {
    await messageStore.send({
      toUserId: otherUserId.value,
      content: content,
      productId: null
    })
    // 发送成功后立即在本地添加消息
    messages.value.push({
      id: Date.now(),
      fromUserId: currentUserId.value,
      toUserId: otherUserId.value,
      content: content,
      createTime: new Date().toISOString()
    })
    scrollToBottom()
    // 后台刷新消息列表（静默失败）
    loadMessages().catch(() => {})
  } catch (e) {
    console.error('发送消息失败', e)
    ElMessage.error(e.message || '发送失败')
  }
}

// 获取对方用户信息
const loadOtherUserInfo = async () => {
  if (!otherUserId.value) return
  try {
    const res = await userStore.getUserById(otherUserId.value)
    if (res && res.data) {
      const user = res.data
      otherUserName.value = user.nickname || user.username || '用户'
      otherUserAvatar.value = user.avatar || 'https://picsum.photos/seed/other/40/40'
      console.log('加载对方用户信息:', otherUserName.value)
    }
  } catch (e) {
    console.error('加载对方用户信息失败', e)
  }
}

onMounted(async () => {
  otherUserId.value = parseInt(route.params.otherUserId)
  
  // 加载对方用户信息
  await loadOtherUserInfo()
  
  // 加载商品上下文信息
  const productId = route.query.productId
  if (productId) {
    try {
      const product = await productStore.getDetail(productId)
      productContext.value = product
      console.log('AI已加载商品上下文:', product?.name)
    } catch (e) {
      console.log('加载商品信息失败，AI将使用通用回复')
    }
  }
  
  if (userStore.userInfo) {
    currentUserId.value = userStore.userInfo.id
  } else {
    try {
      await userStore.fetchUserInfo()
      currentUserId.value = userStore.userInfo.id
    } catch (e) { /* ignore */ }
  }
  if (!currentUserId.value) {
    const token = localStorage.getItem('token')
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]))
        currentUserId.value = payload.userId
      } catch (e) { /* ignore */ }
    }
  }
  await loadMessages()
})
</script>

<style scoped>
.chat-page { min-height: 100vh; background: #F5F5F5; display: flex; flex-direction: column; }
.chat-content { flex: 1; display: flex; max-width: 1000px; margin: 0 auto; width: 100%; overflow: hidden; }

.chat-sidebar {
  width: 300px;
  background: #FFFFFF;
  border-right: 1px solid #EEEEEE;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}
.sidebar-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 16px; border-bottom: 1px solid #EEEEEE;
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
}
.sidebar-header h3 { font-size: 16px; font-weight: 600; color: #FFFFFF; }
.sidebar-close { cursor: pointer; font-size: 18px; color: rgba(255,255,255,0.8); }
.sidebar-close:hover { color: #FFFFFF; }
.sidebar-body { padding: 16px; flex: 1; overflow-y: auto; }
.ai-hint { font-size: 15px; color: #333; margin-bottom: 8px; font-weight: 600; }
.ai-desc { font-size: 12px; color: #999; margin-bottom: 16px; line-height: 1.4; }
.ai-actions { display: flex; flex-direction: column; gap: 8px; margin-bottom: 16px; }
.ai-action-btn {
  padding: 12px 16px; border: 1px solid #EEEEEE; border-radius: 12px;
  background: #FFFFFF; font-size: 14px; cursor: pointer; text-align: left;
  transition: all 0.2s; display: flex; align-items: center; gap: 8px;
}
.ai-action-btn:hover { border-color: #FF6A00; background: #FFF8F0; transform: translateX(4px); }
.ai-action-btn:active { transform: scale(0.98); }
.ai-previews { display: flex; flex-direction: column; gap: 10px; }
.ai-preview-title { font-size: 13px; color: #666; font-weight: 600; margin-bottom: 8px; padding-bottom: 8px; border-bottom: 1px solid #EEEEEE; }
.ai-preview-item { font-size: 13px; padding: 10px 12px; border-radius: 10px; line-height: 1.5; }
.buyer-preview { background: #FFF0E6; border-left: 3px solid #FF6A00; }
.seller-preview { background: #F0F9F0; border-left: 3px solid #52C41A; }
.ai-role { font-weight: 600; display: block; margin-bottom: 4px; font-size: 12px; }
.buyer-preview .ai-role { color: #FF6A00; }
.seller-preview .ai-role { color: #52C41A; }
.ai-content { display: block; color: #333; font-size: 13px; }

.chat-main { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.chat-main.sidebar-open { border-left: 1px solid #EEEEEE; }
.chat-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 20px; background: #FFFFFF; border-bottom: 1px solid #EEEEEE;
}
.back-btn { background: none; border: none; font-size: 16px; color: #666; cursor: pointer; }
.chat-user-info { display: flex; align-items: center; gap: 10px; }
.chat-avatar { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; }
.chat-username { font-size: 16px; font-weight: 600; color: #333; }
.chat-status { display: block; font-size: 12px; color: #52C41A; }
.ai-toggle {
  padding: 8px 16px; border: 1px solid #FF6A00; border-radius: 20px;
  background: #FFFFFF; color: #FF6A00; font-size: 14px; cursor: pointer;
  transition: all 0.2s;
}
.ai-toggle:hover { background: #FFF8F0; }
.ai-toggle.active { background: #FF6A00; color: #FFFFFF; }
.ai-toggle.active:hover { background: #E55D00; }

.chat-messages { flex: 1; padding: 20px; overflow-y: auto; background: #F9F9F9; }
.chat-empty { text-align: center; padding: 80px 0; color: #999; }
.chat-empty span { font-size: 48px; }
.chat-empty p { margin-top: 12px; font-size: 14px; }
.chat-tip { font-size: 12px !important; color: #BBB !important; }

.message-item { margin-bottom: 20px; max-width: 70%; }
.message-item.sent { margin-left: auto; text-align: right; }
.message-item.received { margin-right: auto; text-align: left; }
.message-bubble { padding: 12px 16px; border-radius: 16px; font-size: 14px; line-height: 1.5; display: inline-block; max-width: 100%; word-break: break-word; }
.sent .message-bubble { background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%); color: #FFFFFF; border-bottom-right-radius: 4px; }
.received .message-bubble { background: #FFFFFF; color: #333; border-bottom-left-radius: 4px; box-shadow: 0 2px 4px rgba(0,0,0,0.05); }
.message-bubble.typing { display: flex; gap: 4px; padding: 16px; align-items: center; }
.dot { width: 8px; height: 8px; border-radius: 50%; background: #999; animation: bounce 1.4s infinite both; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce { 0%, 80%, 100% { transform: scale(0); } 40% { transform: scale(1); } }
.message-time { font-size: 11px; color: #BBB; margin-top: 4px; display: block; }

.chat-input-area { background: #FFFFFF; padding: 12px 20px; border-top: 1px solid #EEEEEE; }
.quick-replies { display: flex; gap: 8px; margin-bottom: 8px; flex-wrap: wrap; }
.quick-reply-btn {
  padding: 6px 14px; border: 1px solid #EEEEEE; border-radius: 16px;
  background: #FFFFFF; font-size: 12px; color: #FF6A00; cursor: pointer;
}
.quick-reply-btn:hover { background: #FFF8F0; border-color: #FF6A00; }
.input-row { display: flex; gap: 12px; }
.chat-input { flex: 1; padding: 12px 16px; border: 1px solid #EEEEEE; border-radius: 24px; font-size: 14px; outline: none; }
.chat-input:focus { border-color: #FF6A00; }
.send-btn {
  padding: 0 24px; background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
  color: #FFFFFF; border: none; border-radius: 24px; font-size: 14px; font-weight: 500; cursor: pointer;
}
.send-btn:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
