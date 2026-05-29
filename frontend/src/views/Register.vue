<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <div class="logo-wrapper">
          <User class="logo-icon" />
        </div>
        <h1>校园二手交易平台</h1>
        <p>创建您的账户</p>
      </div>
      <el-form ref="registerFormRef" :model="registerForm" :rules="rules" class="register-form">
        <el-form-item prop="username">
          <el-input
            v-model="registerForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
          />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input
            v-model="registerForm.phone"
            placeholder="请输入手机号"
            prefix-icon="Phone"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="registerForm.password"
            type="password"
            placeholder="请输入密码（字母+数字，6-20位）"
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input
            v-model="registerForm.confirmPassword"
            type="password"
            placeholder="请确认密码"
            prefix-icon="Lock"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="register-btn" @click="register">注册</el-button>
        </el-form-item>
      </el-form>
      <div class="register-footer">
        <span>已有账户？</span>
        <a href="/login" class="login-link">立即登录</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { ElMessage } from 'element-plus'
import { User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const registerForm = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'input' },
    { min: 3, max: 20, message: '用户名长度在3-20之间', trigger: 'input' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'input' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'input' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'input' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d).{6,20}$/, message: '密码必须包含字母和数字', trigger: 'input' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'input' },
    { validator: (rule, value, callback) => {
      if (value !== registerForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }, trigger: 'input' }
  ]
}

const registerFormRef = ref(null)

const register = async () => {
  if (!registerFormRef.value) return
  
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await userStore.register({
          username: registerForm.username,
          phone: registerForm.phone,
          password: registerForm.password
        })
        ElMessage.success('注册成功')
        router.push('/login')
      } catch (e) {
        ElMessage.error(e.message || '注册失败')
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-box {
  background: #fff;
  border-radius: 16px;
  padding: 40px;
  width: 450px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.15);
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

.register-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.register-header p {
  color: #999;
  font-size: 14px;
}

.register-form {
  margin-bottom: 20px;
}

.register-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  border-radius: 8px;
}

.register-footer {
  text-align: center;
  color: #999;
  font-size: 14px;
}

.login-link {
  color: #667eea;
  margin-left: 8px;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}
</style>