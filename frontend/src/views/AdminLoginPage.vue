<template>
  <div class="admin-login-page">
    <div class="admin-login-card">
      <h2>🔐 管理员登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="管理员账号" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="danger" size="large" style="width:100%" :loading="loading" @click="handleLogin">管理员登录</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center">
        <router-link to="/login">返回用户登录</router-link>
      </div>
      <div class="admin-tip">
        <el-text type="info" size="small">默认账号: root / 123456</el-text>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入管理员账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    await userStore.adminLogin(form.username, form.password)
    ElMessage.success('管理员登录成功')
    router.push('/admin')
  } catch (e) {
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.admin-login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2c3e50 0%, #4ca1af 100%);
}
.admin-login-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}
.admin-login-card h2 { text-align: center; margin-bottom: 30px; color: #333; }
.admin-tip { text-align: center; margin-top: 16px; }
</style>
