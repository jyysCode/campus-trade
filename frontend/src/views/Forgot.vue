<template>
  <div class="forgot-page">
    <div class="forgot-container">
      <div class="forgot-header">
        <div class="logo">
          <span class="logo-icon">📦</span>
          <span class="logo-text">校园交易</span>
        </div>
        <h2 class="forgot-title">忘记密码</h2>
        <p class="forgot-subtitle">通过手机号找回密码</p>
      </div>

      <form class="forgot-form" @submit.prevent="handleReset">
        <div class="form-group">
          <label class="form-label">手机号</label>
          <div class="input-wrapper">
            <span class="input-icon">📱</span>
            <input v-model="form.phone" type="tel" class="form-input" placeholder="请输入注册手机号" />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">验证码</label>
          <div class="captcha-row">
            <input v-model="form.code" type="text" class="form-input captcha-input" placeholder="请输入验证码" />
            <img :src="captchaUrl" alt="验证码" class="captcha-img" @click="refreshCaptcha" title="点击刷新" />
          </div>
        </div>

        <div class="form-group">
          <label class="form-label">新密码</label>
          <div class="input-wrapper">
            <span class="input-icon">🔐</span>
            <input v-model="form.newPassword" :type="showPassword ? 'text' : 'password'" class="form-input" placeholder="请输入新密码" />
          </div>
        </div>

        <button type="submit" class="reset-btn" :disabled="isLoading">
          <span v-if="isLoading">重置中...</span>
          <span v-else>重置密码</span>
        </button>
      </form>

      <div class="forgot-footer">
        <span>还记得密码？</span>
        <span class="login-link" @click="goLogin">立即登录</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({
  phone: '',
  code: '',
  newPassword: ''
})
const showPassword = ref(false)
const isLoading = ref(false)
const captchaUrl = ref('/api/captcha?t=' + Date.now())

const handleReset = async () => {
  if (!form.phone) { alert('请输入手机号'); return }
  if (!form.code) { alert('请输入验证码'); return }
  if (!form.newPassword) { alert('请输入新密码'); return }
  if (form.newPassword.length < 6) { alert('密码至少6位'); return }

  isLoading.value = true
  try {
    const res = await fetch('/api/user/reset-password', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        phone: form.phone,
        code: form.code,
        newPassword: form.newPassword
      })
    })
    const data = await res.json()
    if (data.code === 200) {
      alert('密码重置成功，请使用新密码登录')
      router.push('/login')
    } else {
      alert(data.message || '重置失败')
      refreshCaptcha()
    }
  } catch (e) {
    alert('重置失败，请检查网络连接')
  } finally {
    isLoading.value = false
  }
}

const refreshCaptcha = () => {
  captchaUrl.value = '/api/captcha?t=' + Date.now()
}

const goLogin = () => { router.push('/login') }
</script>

<style scoped>
.forgot-page { min-height: 100vh; display: flex; align-items: center; justify-content: center; background: #F5F5F5; }
.forgot-container { background: #FFFFFF; border-radius: 20px; padding: 40px; width: 420px; box-shadow: 0 10px 40px rgba(0,0,0,0.1); }
.forgot-header { text-align: center; margin-bottom: 32px; }
.logo { display: flex; align-items: center; justify-content: center; margin-bottom: 16px; }
.logo-icon { font-size: 36px; margin-right: 8px; }
.logo-text { font-size: 28px; font-weight: 700; color: #FF6A00; }
.forgot-title { font-size: 24px; font-weight: 600; color: #333; margin-bottom: 8px; }
.forgot-subtitle { font-size: 14px; color: #999; }
.forgot-form { margin-bottom: 24px; }
.form-group { margin-bottom: 20px; }
.form-label { display: block; font-size: 14px; font-weight: 500; color: #333; margin-bottom: 8px; }
.input-wrapper { position: relative; }
.form-input { width: 100%; padding: 14px 16px 14px 44px; border: 1px solid #EEEEEE; border-radius: 12px; font-size: 14px; outline: none; transition: border-color 0.2s; }
.form-input:focus { border-color: #FF6A00; }
.form-input::placeholder { color: #BBB; }
.input-icon { position: absolute; left: 14px; top: 50%; transform: translateY(-50%); font-size: 18px; }
.captcha-row { display: flex; gap: 12px; }
.captcha-input { flex: 1; padding: 14px 16px !important; padding-left: 16px !important; }
.captcha-img { width: 120px; height: 46px; border-radius: 8px; cursor: pointer; border: 1px solid #EEEEEE; object-fit: cover; }
.reset-btn { width: 100%; padding: 14px; background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%); color: #FFFFFF; border: none; border-radius: 12px; font-size: 16px; font-weight: 500; cursor: pointer; transition: opacity 0.2s; }
.reset-btn:hover:not(:disabled) { opacity: 0.9; }
.reset-btn:disabled { opacity: 0.6; cursor: not-allowed; }
.forgot-footer { text-align: center; font-size: 14px; color: #666; }
.login-link { color: #FF6A00; cursor: pointer; margin-left: 4px; }
</style>
