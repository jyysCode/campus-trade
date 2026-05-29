<template>
  <div class="profile-page">
    <div class="profile-content">
      <div class="profile-header">
        <div class="user-avatar-wrapper">
          <img :src="getUserAvatar(userStore.userInfo?.id, userStore.userInfo?.username)" class="user-avatar" />
          <div class="avatar-overlay" @click="showAvatarDialog = true">
            <el-icon><Camera /></el-icon>
            <span>更换头像</span>
          </div>
        </div>
        <div class="user-info">
          <h2 class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h2>
          <p class="user-phone">{{ userStore.userInfo?.phone }}</p>
        </div>
      </div>

      <div class="profile-sections">
        <!-- 我的商品 - 只显示一行 -->
        <div class="profile-card">
          <div class="card-header">
            <h3 class="card-title">我的商品 <span class="count-badge" v-if="myProducts.length > 0">{{ myProducts.length }}</span></h3>
            <a href="/my-products" class="view-more">查看更多 ></a>
          </div>
          <div class="my-products" v-if="myProducts.length > 0">
            <div v-for="product in myProducts.slice(0, 4)" :key="product.id" class="product-item" @click="goDetail(product.id)">
              <img :src="getProductImage(product)" :alt="product.name" class="product-thumb" />
              <div class="product-info">
                <p class="product-name">{{ product.name }}</p>
                <p class="product-price">¥{{ product.price }}</p>
                <span class="product-status" :class="'status-' + product.status">{{ product.status === 0 ? '待审核' : product.status === 1 ? '已通过' : '已拒绝' }}</span>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <span>📦</span>
            <p>暂无商品</p>
            <button class="go-btn" @click="goPublish">去发布</button>
          </div>
        </div>

        <!-- 我的收藏 - 只显示一行 -->
        <div class="profile-card">
          <div class="card-header">
            <h3 class="card-title">我的收藏 <span class="count-badge" v-if="favoriteProducts.length > 0">{{ favoriteProducts.length }}</span></h3>
            <a href="/favorites" class="view-more">查看更多 ></a>
          </div>
          <div class="my-products" v-if="favoriteProducts.length > 0">
            <div v-for="product in favoriteProducts.slice(0, 4)" :key="product.id" class="product-item" @click="goDetail(product.id)">
              <img :src="getProductImage(product)" :alt="product.name" class="product-thumb" />
              <div class="product-info">
                <p class="product-name">{{ product.name }}</p>
                <p class="product-price">¥{{ product.price }}</p>
                <span class="product-seller">{{ product.sellerName }}</span>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <span>❤️</span>
            <p>暂无收藏</p>
            <button class="go-btn" @click="$router.push('/')">去逛逛</button>
          </div>
        </div>

        <div class="profile-card">
          <h3 class="card-title">个人信息</h3>
          <el-form :model="userForm" label-width="80px">
            <el-form-item label="用户名">
              <el-input v-model="userForm.username" disabled />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="userForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="updateInfo">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="profile-card">
          <h3 class="card-title">修改密码</h3>
          <el-form :model="passwordForm" label-width="80px">
            <el-form-item label="新密码">
              <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码">
              <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>

    <!-- 更换头像对话框 -->
    <el-dialog v-model="showAvatarDialog" title="更换头像" width="500px">
      <div class="avatar-select">
        <p class="select-tip">请选择以下头像之一：</p>
        <div class="avatar-grid">
          <div
            v-for="(avatar, index) in avatarOptions"
            :key="index"
            :class="['avatar-option', { selected: selectedAvatar === avatar }]"
            @click="selectedAvatar = avatar"
          >
            <img :src="avatar" />
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="showAvatarDialog = false">取消</el-button>
        <el-button type="primary" @click="updateAvatar">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useProductStore } from '../stores/product'
import { ElMessage } from 'element-plus'
import { Camera } from '@element-plus/icons-vue'
import { getProductImage, getUserAvatar } from '../utils/image'

const router = useRouter()
const userStore = useUserStore()
const productStore = useProductStore()

const myProducts = ref([])
const pendingProducts = computed(() => myProducts.value.filter(p => p.status === 0))
const approvedProducts = computed(() => myProducts.value.filter(p => p.status === 1))
const rejectedProducts = computed(() => myProducts.value.filter(p => p.status === 2))
const favoriteProducts = ref([])
const userForm = reactive({ username: '', phone: '' })
const passwordForm = reactive({ newPassword: '', confirmPassword: '' })

// 头像相关
const showAvatarDialog = ref(false)
const selectedAvatar = ref('')
const avatarOptions = [
  'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&h=200&fit=crop',
  'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop',
  'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=200&h=200&fit=crop',
  'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=200&h=200&fit=crop',
  'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&h=200&fit=crop',
  'https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&h=200&fit=crop',
  'https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=200&h=200&fit=crop',
  'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&h=200&fit=crop',
  'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?w=200&h=200&fit=crop',
]

const goDetail = (id) => router.push(`/product/${id}`)
const goEdit = (id) => router.push(`/publish?editId=${id}`)
const goPublish = () => router.push('/publish')

const updateInfo = async () => {
  try {
    await userStore.update({ phone: userForm.phone })
    ElMessage.success('修改成功')
  } catch (e) { ElMessage.error(e.message || '修改失败') }
}

const changePassword = async () => {
  if (!passwordForm.newPassword || passwordForm.newPassword.length < 6) {
    ElMessage.error('密码至少6位'); return
  }
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    ElMessage.error('两次密码不一致'); return
  }
  try {
    await userStore.update({ password: passwordForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    localStorage.removeItem('token')
    passwordForm.newPassword = ''
    passwordForm.confirmPassword = ''
    setTimeout(() => router.push('/login'), 1000)
  } catch (e) { ElMessage.error(e.message || '修改失败') }
}

const updateAvatar = async () => {
  if (!selectedAvatar.value) {
    ElMessage.warning('请选择头像')
    return
  }
  try {
    await userStore.update({ avatar: selectedAvatar.value })
    userStore.userInfo.avatar = selectedAvatar.value
    ElMessage.success('头像更换成功')
    showAvatarDialog.value = false
  } catch (e) {
    ElMessage.error('更换失败')
  }
}

onMounted(async () => {
  try {
    await userStore.fetchUserInfo()
    userForm.username = userStore.userInfo?.username || ''
    userForm.phone = userStore.userInfo?.phone || ''
    selectedAvatar.value = userStore.userInfo?.avatar || ''
  } catch (e) { /* ignore */ }
  try {
    const res = await productStore.my()
    myProducts.value = res?.records || res || []
  } catch (e) { /* ignore */ }
  // 获取收藏列表
  try {
    const token = localStorage.getItem('token')
    const favRes = await fetch('/api/favorite/list', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const favData = await favRes.json()
    if (favData.code === 200) {
      favoriteProducts.value = favData.data?.records || favData.data || []
    }
  } catch (e) { /* ignore */ }
})
</script>

<style scoped>
.profile-page { min-height: 100vh; background: #F5F5F5; }
.profile-content { max-width: 900px; margin: 0 auto; padding: 24px 16px; }
.profile-header { background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%); border-radius: 16px; padding: 32px; display: flex; align-items: center; gap: 24px; margin-bottom: 24px; }

.user-avatar-wrapper { 
  position: relative;
  width: 80px; 
  height: 80px; 
  border-radius: 50%; 
  overflow: hidden;
  border: 3px solid rgba(255,255,255,0.5);
  cursor: pointer;
}

.user-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
  color: #fff;
  font-size: 12px;
}

.user-avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay .el-icon {
  font-size: 20px;
  margin-bottom: 4px;
}

.user-info { color: #FFFFFF; }
.username { font-size: 24px; font-weight: 700; margin-bottom: 4px; }
.user-phone { font-size: 15px; opacity: 0.9; }
.profile-sections { display: flex; flex-direction: column; gap: 20px; }
.profile-card { background: #FFFFFF; border-radius: 14px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); }
.card-title { font-size: 18px; font-weight: 600; color: #333; margin-bottom: 0; display: flex; align-items: center; gap: 8px; }
.card-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.view-more { font-size: 14px; color: #FF6A00; text-decoration: none; }
.view-more:hover { text-decoration: underline; }
.count-badge { background: #FF6A00; color: #fff; font-size: 12px; padding: 2px 8px; border-radius: 10px; }
.my-products { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.product-seller { font-size: 11px; color: #999; }
.product-item { cursor: pointer; border-radius: 10px; overflow: hidden; transition: transform 0.2s; }
.product-item:hover { transform: translateY(-2px); }
.product-thumb { width: 100%; height: 140px; object-fit: cover; background: #F5F5F5; }
.product-info { padding: 8px 4px; }
.product-name { font-size: 13px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; margin-bottom: 4px; }
.product-price { font-size: 16px; color: #FF6A00; font-weight: 700; margin-bottom: 4px; }
.product-status { font-size: 11px; padding: 2px 6px; border-radius: 4px; }
.status-0 { background: #FEF08A; color: #854D0E; }
.status-1 { background: #BBF7D0; color: #166534; }
.status-2 { background: #FECACA; color: #991B1B; }
.empty-state { width: 100%; text-align: center; padding: 40px; color: #999; }
.empty-state span { font-size: 36px; display: block; margin-bottom: 8px; }
.go-btn { margin-top: 12px; padding: 8px 24px; background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%); color: #FFFFFF; border: none; border-radius: 20px; font-size: 14px; cursor: pointer; }

/* 头像选择对话框 */
.avatar-select { padding: 20px; }
.select-tip { margin-bottom: 16px; color: #666; }
.avatar-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
.avatar-option { 
  width: 100%; 
  aspect-ratio: 1; 
  border-radius: 50%; 
  overflow: hidden; 
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.3s;
}
.avatar-option:hover { border-color: #FF6A00; }
.avatar-option.selected { border-color: #FF6A00; box-shadow: 0 0 0 4px rgba(255,106,0,0.2); }
.avatar-option img { width: 100%; height: 100%; object-fit: cover; }
</style>
