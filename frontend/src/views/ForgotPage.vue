<template>
  <div class="forgot-page">
    <div class="forgot-card">
      <h2>重置密码</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="0">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="注册手机号" size="large" />
        </el-form-item>
        <el-form-item prop="code">
          <div style="display:flex;gap:10px">
            <el-input v-model="form.code" placeholder="验证码" size="large" />
            <img :src="captchaUrl" class="captcha-img" @click="refreshCaptcha" title="点击刷新" />
          </div>
        </el-form-item>
        <el-form-item prop="newPassword">
          <el-input v-model="form.newPassword" type="password" placeholder="新密码(字母+数字,6-20位)" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width:100%" :loading="loading" @click="handleSubmit">重置密码</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align:center">
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { resetPassword, getCaptchaUrl } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const captchaUrl = ref(getCaptchaUrl())
const form = reactive({ phone: '', code: '', newPassword: '' })
const rules = {
  phone: [{ required: true, pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  newPassword: [{ required: true, pattern: /^(?=.*[a-zA-Z])(?=.*\d).{6,20}$/, message: '密码需包含字母和数字，6-20位', trigger: 'blur' }]
}
const refreshCaptcha = () => { captchaUrl.value = getCaptchaUrl() }

const handleSubmit = async () => {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await resetPassword(form)
    if (res.data.code === 200) {
      ElMessage.success('密码重置成功，请登录')
      router.push('/login')
    } else {
      ElMessage.error(res.data.message)
      refreshCaptcha()
    }
  } catch (e) { refreshCaptcha() }
  finally { loading.value = false }
}
</script>

<style scoped>
.forgot-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.forgot-card {
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  width: 400px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.15);
}
.forgot-card h2 { text-align: center; margin-bottom: 30px; color: #333; }
.captcha-img { height: 40px; cursor: pointer; border-radius: 4px; }
</style>
