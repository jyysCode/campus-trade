<template>
  <div class="profile-page">
    <h1>👤 个人中心</h1>
    <div class="profile-card">
      <el-form :model="form" label-width="80px" style="max-width:500px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="设置昵称" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="设置邮箱" />
        </el-form-item>
        <el-form-item label="头像">
          <el-input v-model="form.avatar" placeholder="头像URL" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="profile-links">
      <router-link to="/orders" class="link-item">📋 我的订单</router-link>
      <router-link to="/favorites" class="link-item">⭐ 我的收藏</router-link>
      <router-link to="/publish" class="link-item">📝 发布商品</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useUserStore } from '../stores/user'
import { updateUserInfo } from '../api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const saving = ref(false)
const form = reactive({ username: '', phone: '', nickname: '', email: '', avatar: '' })

onMounted(() => {
  if (userStore.userInfo) {
    Object.assign(form, userStore.userInfo)
  }
})

const handleSave = async () => {
  saving.value = true
  try {
    await updateUserInfo(form)
    await userStore.fetchUserInfo()
    ElMessage.success('保存成功')
  } catch (e) {}
  saving.value = false
}
</script>

<style scoped>
.profile-page { max-width: 800px; margin: 0 auto; padding: 20px; }
.profile-page h1 { margin-bottom: 20px; }
.profile-card { background: #fff; padding: 30px; border-radius: 8px; margin-bottom: 20px; }
.profile-links { display: flex; flex-direction: column; gap: 12px; }
.link-item { background: #fff; padding: 16px 20px; border-radius: 8px; font-size: 15px; transition: all 0.2s; }
.link-item:hover { background: #ecf5ff; color: #409eff; }
</style>
