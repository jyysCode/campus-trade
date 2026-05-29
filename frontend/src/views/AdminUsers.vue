<template>
  <div>
    <h2>用户管理</h2>
    <el-table :data="users" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="phone" label="手机号" width="130" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="userType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="row.userType === 1 ? 'danger' : 'info'" size="small">{{ row.userType === 1 ? '管理员' : '用户' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">{{ row.status === 0 ? '正常' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="deleted" label="删除状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.deleted === 1 ? 'danger' : 'success'" size="small">{{ row.deleted === 1 ? '已删除' : '正常' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button v-if="row.deleted !== 1" type="primary" size="small" text @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="row.userType !== 1 && row.deleted !== 1" type="danger" size="small" text @click="handleDelete(row.id)">删除</el-button>
          <span v-if="row.deleted === 1" class="deleted-hint">已删除的用户</span>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑用户对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑用户" width="500px">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="editForm.status">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, deleteUser, updateUserAdmin } from '../api'
import { ElMessage, ElMessageBox } from 'element-plus'

const users = ref([])
const loading = ref(false)
const editDialogVisible = ref(false)
const editForm = ref({})

const loadData = async () => {
  loading.value = true
  try {
    const res = await getUserList()
    users.value = (res.data.data || []).map(u => ({ ...u, password: null }))
  } catch (e) {
    console.error('加载用户列表失败', e)
    ElMessage.error('加载用户列表失败')
  }
  loading.value = false
}

const handleEdit = (row) => {
  editForm.value = { ...row }
  editDialogVisible.value = true
}

const saveEdit = async () => {
  try {
    await updateUserAdmin(editForm.value.id, {
      nickname: editForm.value.nickname,
      phone: editForm.value.phone,
      status: editForm.value.status
    })
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    loadData()
  } catch (e) {
    console.error('更新失败', e)
    ElMessage.error('更新失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该用户？', '提示')
    await deleteUser(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('删除失败', e)
      ElMessage.error('删除失败')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
h2 { margin-bottom: 16px; }
.deleted-hint { color: #999; font-size: 12px; }
</style>
