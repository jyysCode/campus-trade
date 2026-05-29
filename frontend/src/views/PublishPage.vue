<template>
  <div class="publish-page">
    <h1>📝 发布商品</h1>
    <div class="publish-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px" style="max-width:600px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="form.category" placeholder="选择分类" style="width:100%">
            <el-option v-for="c in categoryOptions" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="售价" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :precision="2" :step="10" style="width:200px" />
        </el-form-item>
        <el-form-item label="原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" :step="10" style="width:200px" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="1" style="width:200px" />
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-input v-model="form.image" placeholder="图片URL或上传" />
          <el-upload :action="'/api/upload'" :headers="uploadHeaders" :show-file-list="false" :on-success="onUploadSuccess" accept="image/*" style="margin-top:8px">
            <el-button size="small" type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="成色">
          <el-select v-model="form.condition" placeholder="选择成色" style="width:200px">
            <el-option label="全新" value="全新" />
            <el-option label="几乎全新" value="几乎全新" />
            <el-option label="9成新" value="9成新" />
            <el-option label="8成新" value="8成新" />
            <el-option label="7成新及以下" value="7成新及以下" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="详细描述商品信息" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">发布商品</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { addProduct, getCategories, uploadFile } from '../api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const categoryOptions = ref([])
const form = reactive({ name: '', category: '', price: null, originalPrice: null, stock: 1, image: '', condition: '', description: '' })
const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  image: [{ required: true, message: '请上传图片', trigger: 'blur' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }]
}
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${localStorage.getItem('token')}` }))

const onUploadSuccess = (res) => {
  if (res.code === 200) { form.image = res.data; ElMessage.success('上传成功') }
  else ElMessage.error(res.message || '上传失败')
}

const handleSubmit = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    const res = await addProduct(form)
    if (res.data.code === 200) {
      ElMessage.success('发布成功，等待审核')
      router.push('/profile')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (e) {}
  submitting.value = false
}

onMounted(async () => {
  try {
    const res = await getCategories()
    categoryOptions.value = res.data.data || []
  } catch (e) {}
})
</script>

<style scoped>
.publish-page { max-width: 800px; margin: 0 auto; padding: 20px; }
.publish-page h1 { margin-bottom: 20px; }
.publish-card { background: #fff; padding: 30px; border-radius: 8px; }
</style>
