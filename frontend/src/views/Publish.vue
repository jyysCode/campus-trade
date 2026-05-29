<template>
  <div class="publish-page">
    <div class="publish-content">
      <div class="publish-card">
        <h1 class="publish-title">发布商品</h1>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
          <el-form-item label="商品名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="100" show-word-limit />
          </el-form-item>
          <el-form-item label="商品分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择分类" style="width:100%">
              <el-option label="电子产品" value="电子产品" />
              <el-option label="图书教材" value="图书教材" />
              <el-option label="生活用品" value="生活用品" />
              <el-option label="服饰鞋包" value="服饰鞋包" />
              <el-option label="运动器材" value="运动器材" />
              <el-option label="美妆护肤" value="美妆护肤" />
              <el-option label="数码配件" value="数码配件" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="商品价格" prop="price">
            <el-input v-model="form.price" placeholder="请输入商品价格">
              <template #prefix>¥</template>
            </el-input>
          </el-form-item>
          <el-form-item label="商品库存" prop="stock">
            <el-input v-model="form.stock" placeholder="请输入库存数量">
              <template #suffix>件</template>
            </el-input>
          </el-form-item>
          <el-form-item label="商品图片">
            <div class="upload-area">
              <input type="file" ref="fileInput" accept="image/*" @change="handleFileChange" style="display:none" />
              <div v-if="!imagePreview" class="upload-box" @click="triggerUpload">
                <span class="upload-icon">📷</span>
                <span class="upload-text">点击上传商品图片（可选）</span>
                <span class="upload-hint">不上传将使用默认图片 | jpg/png/gif，最大 10MB</span>
              </div>
              <div v-else class="image-preview">
                <img :src="imagePreview" alt="预览" />
                <div class="preview-actions">
                  <el-button size="small" @click="triggerUpload">重新选择</el-button>
                  <el-button size="small" type="danger" @click="removeImage">删除</el-button>
                </div>
              </div>
              <div v-if="uploading" class="upload-progress">
                <el-progress :percentage="uploadProgress" />
                <span>上传中...</span>
              </div>
            </div>
          </el-form-item>
          <el-form-item label="商品描述" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请详细描述商品信息" maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" class="submit-btn" @click="submit" :loading="submitting" round>
              {{ submitting ? '发布中...' : '立即发布' }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const fileInput = ref(null)
const imagePreview = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)
const submitting = ref(false)
const uploadedUrl = ref('')

const form = reactive({
  name: '',
  category: '',
  price: '',
  stock: '',
  image: '',
  description: ''
})

const validatePrice = (rule, value, callback) => {
  const num = parseFloat(value)
  if (isNaN(num) || num <= 0) {
    callback(new Error('价格必须大于0'))
  } else if (num > 999999.99) {
    callback(new Error('价格不能超过999999.99'))
  } else {
    callback()
  }
}

const validateStock = (rule, value, callback) => {
  const num = parseInt(value)
  if (isNaN(num) || num < 1 || num !== parseFloat(value)) {
    callback(new Error('库存必须为大于0的整数'))
  } else if (num > 99999) {
    callback(new Error('库存不能超过99999'))
  } else {
    callback()
  }
}

const rules = {
  name: [
    { required: true, message: '请输入商品名称', trigger: 'blur' },
    { min: 2, max: 100, message: '名称长度在2-100之间', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择商品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入商品价格', trigger: 'blur' },
    { validator: validatePrice, trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { validator: validateStock, trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入商品描述', trigger: 'blur' }
  ]
}

const triggerUpload = () => { fileInput.value.click() }

const handleFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  if (!file.type.startsWith('image/')) { ElMessage.error('请选择图片文件'); return }
  if (file.size > 10 * 1024 * 1024) { ElMessage.error('图片大小不能超过10MB'); return }

  const reader = new FileReader()
  reader.onload = (ev) => { imagePreview.value = ev.target.result }
  reader.readAsDataURL(file)

  uploading.value = true
  uploadProgress.value = 0
  const formData = new FormData()
  formData.append('file', file)

  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/upload/image', {
      method: 'POST',
      headers: token ? { 'Authorization': 'Bearer ' + token } : {},
      body: formData
    })
    const data = await res.json()
    if (data.code === 200) {
      uploadedUrl.value = data.data
      form.image = data.data
      uploadProgress.value = 100
      ElMessage.success('图片上传成功')
    } else {
      ElMessage.error(data.message || '上传失败')
      imagePreview.value = ''
    }
  } catch (err) {
    ElMessage.error('上传失败，请检查网络')
    imagePreview.value = ''
  } finally {
    uploading.value = false
  }
}

const removeImage = () => {
  imagePreview.value = ''
  uploadedUrl.value = ''
  form.image = ''
  fileInput.value.value = ''
}

const submit = async () => {
  if (!formRef.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    const defaultImages = [
      'https://picsum.photos/seed/default1/400/400',
      'https://picsum.photos/seed/default2/400/400',
      'https://picsum.photos/seed/default3/400/400'
    ]
    const finalImage = form.image || defaultImages[Math.floor(Math.random() * defaultImages.length)]

    const token = localStorage.getItem('token')
    const res = await fetch('/api/product/add', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + token
      },
      body: JSON.stringify({
        name: form.name,
        category: form.category,
        price: parseFloat(form.price),
        stock: parseInt(form.stock),
        image: finalImage,
        description: form.description
      })
    })
    const data = await res.json()
    if (data.code === 200) {
      ElMessage.success('您的商品正在审核中，请耐心等待')
      router.push('/')
    } else {
      ElMessage.error(data.message || '发布失败')
    }
  } catch (e) {
    ElMessage.error('发布失败，请检查网络')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.publish-page { min-height: 100vh; background: #F5F5F5; }
.publish-content { max-width: 700px; margin: 0 auto; padding: 32px 16px; }
.publish-card { background: #FFFFFF; border-radius: 16px; padding: 40px; box-shadow: 0 2px 12px rgba(0,0,0,0.06); }
.publish-title { font-size: 26px; font-weight: 700; color: #333; text-align: center; margin-bottom: 32px; }
.upload-area { width: 100%; }
.upload-box { width: 100%; min-height: 140px; border: 2px dashed #DDD; border-radius: 12px; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; transition: all 0.3s; background: #FAFAFA; }
.upload-box:hover { border-color: #FF6A00; background: #FFF8F0; }
.upload-icon { font-size: 36px; margin-bottom: 8px; }
.upload-text { font-size: 14px; color: #666; margin-bottom: 4px; }
.upload-hint { font-size: 12px; color: #BBB; }
.image-preview { position: relative; border-radius: 12px; overflow: hidden; }
.image-preview img { width: 100%; max-height: 300px; object-fit: contain; background: #F5F5F5; border-radius: 12px; }
.preview-actions { display: flex; justify-content: center; gap: 12px; margin-top: 12px; }
.upload-progress { margin-top: 12px; text-align: center; font-size: 12px; color: #999; }
.submit-btn { width: 100% !important; height: 48px; font-size: 16px; background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%) !important; border: none !important; }
.submit-btn:hover { opacity: 0.9; }
</style>