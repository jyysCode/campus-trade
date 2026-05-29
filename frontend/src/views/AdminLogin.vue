<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-box">
        <div class="login-header">
          <img src="https://picsum.photos/80/80?random=100" alt="Logo" class="logo">
          <h1>校园二手交易平台</h1>
          <p>管理员登录</p>
        </div>
        <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入管理员账号"
              prefix-icon="User"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入管理员密码"
              prefix-icon="Lock"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" class="login-btn" @click="login">登录</el-button>
          </el-form-item>
        </el-form>
        <div class="login-footer">
          <a href="/login" class="back-link">返回用户登录</a>
        </div>
      </div>
      <div class="test-accounts">
        <h3>管理员账号</h3>
        <div class="account-item">
          <span class="account-type">账号</span>
          <span class="account-info">root</span>
        </div>
        <div class="account-item">
          <span class="account-type">密码</span>
          <span class="account-info">123456</span>
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

const router = useRouter()
const userStore = useUserStore()

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入管理员账号', trigger: 'input' }
  ],
  password: [
    { required: true, message: '请输入管理员密码', trigger: 'input' }
  ]
}

const loginFormRef = ref(null)

const login = async () => {
  if (!loginFormRef.value) return
  
  loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.adminLogin(loginForm)
        ElMessage.success('登录成功')
        router.push('/admin')
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
  width: 240px;
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
  gap: 6px;
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

.logo {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin-bottom: 16px;
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
}

.login-footer {
  text-align: center;
  color: #999;
  font-size: 14px;
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

.back-link {
  color: #FF6A00;
  text-decoration: none;
}

.back-link:hover {
  text-decoration: underline;
}
</style>