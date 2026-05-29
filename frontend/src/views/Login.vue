<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-box">
        <div class="login-header">
          <div class="logo-wrapper">
            <ShoppingBag class="logo-icon" />
          </div>
          <h1>校园二手交易平台</h1>
          <p>登录您的账户</p>
        </div>
        <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入手机号"
              prefix-icon="Phone"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码（字母+数字）"
              prefix-icon="Lock"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" @click="login">登录</el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <span>还没有账户？</span>
          <a href="/register" class="register-link">立即注册</a>
          <span class="divider">|</span>
          <a href="/admin/login" class="admin-link">管理员登录</a>
        </div>
      </div>
      <div class="test-accounts">
        <h3>测试账号</h3>
        <div class="account-item">
          <span class="account-type">用户账号</span>
          <span class="account-info">13800138001 / 123456</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { ShoppingBag } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'input' },
    { min: 3, max: 20, message: '用户名长度在3-20之间', trigger: 'input' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'input' },
    { min: 6, max: 20, message: '密码长度在6-20之间', trigger: 'input' }
  ]
}

const loginFormRef = ref(null)

const login = async () => {
  if (!loginFormRef.value) return
  
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        router.push('/')
      } catch (e) {
        ElMessage.error(e.message || '登录失败')
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
}

.login-wrapper {
  display: flex;
  gap: 40px;
  align-items: flex-start;
}

.login-box {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.test-accounts {
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  padding: 30px;
  width: 280px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.test-accounts h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 20px;
  text-align: center;
  border-bottom: 2px solid #FF6A00;
  padding-bottom: 10px;
}

.account-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 15px;
}

.account-type {
  font-size: 14px;
  color: #666;
  font-weight: bold;
}

.account-info {
  font-size: 14px;
  color: #FF6A00;
  font-family: monospace;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
}

.logo-icon {
  width: 28px;
  height: 28px;
  color: #fff;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  color: #999;
  font-size: 14px;
}

.login-form {
  margin-bottom: 20px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #E55F00 0%, #E68800 100%);
}

.login-footer {
  text-align: center;
  color: #999;
  font-size: 14px;
}

.register-link {
  color: #FF6A00;
  margin-left: 8px;
  text-decoration: none;
}

.register-link:hover {
  text-decoration: underline;
}

.divider {
  margin: 0 8px;
  color: #ddd;
}

.admin-link {
  color: #FF6A00;
  text-decoration: none;
}

.admin-link:hover {
  text-decoration: underline;
}
</style>