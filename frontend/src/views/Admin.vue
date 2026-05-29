<template>
  <div class="admin-container">
    <div class="admin-sidebar">
      <div class="sidebar-header">
        <img src="https://picsum.photos/seed/admin/60/60" alt="Admin Logo" class="logo">
        <h2>管理后台</h2>
      </div>
      <el-menu :default-active="activeMenu" class="sidebar-menu" mode="vertical">
        <el-sub-menu index="users">
          <template #title>
            <el-icon class="menu-icon"><User /></el-icon>
            <span>用户管理</span>
          </template>
          <el-menu-item index="user-list" @click="handleSubMenuClick('users', 'list')">用户列表</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="products">
          <template #title>
            <el-icon class="menu-icon"><ShoppingBag /></el-icon>
            <span>商品管理</span>
          </template>
          <el-menu-item index="product-list" @click="handleSubMenuClick('products', 'list')">商品列表</el-menu-item>
          <el-menu-item index="product-pending" @click="handleSubMenuClick('products', 'pending')">待审核商品</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="orders">
          <template #title>
            <el-icon class="menu-icon"><Document /></el-icon>
            <span>订单管理</span>
          </template>
          <el-menu-item index="order-list" @click="handleSubMenuClick('orders', 'list')">订单列表</el-menu-item>
        </el-sub-menu>
      </el-menu>
      <div class="sidebar-footer">
        <el-button type="danger" @click="handleLogout" plain>退出登录</el-button>
      </div>
    </div>
    <div class="admin-content">
      <div class="content-header">
        <h1>{{ pageTitle }}</h1>
      </div>
      <div class="content-body">
        <div v-if="activeMenu === 'users'" class="panel">
          <div class="panel-header">
            <h3>用户列表</h3>
            <div class="header-actions">
              <el-input 
                v-model="searchUser" 
                placeholder="搜索用户名或手机号" 
                class="search-input"
                @keyup.enter="loadUsers()"
              >
                <template #append>
                  <el-button @click="loadUsers()">搜索</el-button>
                </template>
              </el-input>
              <el-button type="primary" @click="addUser">
                <el-icon><Plus /></el-icon>
                <span>添加用户</span>
              </el-button>
            </div>
          </div>
          <el-table :data="users" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="userType" label="用户类型" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.userType === 1 ? 'danger' : 'primary'">
                  {{ scope.row.userType === 1 ? '管理员' : '普通用户' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="160">
              <template #default="scope">
                <el-button 
                  size="small" 
                  icon="Edit" 
                  @click="editUser(scope.row)"
                  title="编辑"
                ></el-button>
                <el-button 
                  size="small" 
                  icon="Delete" 
                  type="danger" 
                  @click="deleteUser(scope.row.id)"
                  title="删除"
                ></el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="users.length === 0" class="empty-tip">
            <div class="empty-icon">👥</div>
            <p>暂无用户数据</p>
          </div>
        </div>
        <div v-if="activeMenu === 'products'" class="panel">
          <div class="panel-header">
            <h3>{{ productsSubMenu === 'pending' ? '待审核商品' : '商品列表' }}</h3>
            <div class="header-actions">
              <el-input 
                v-model="searchProduct" 
                placeholder="搜索商品名称" 
                class="search-input"
                @keyup.enter="loadProducts()"
              >
                <template #append>
                  <el-button @click="loadProducts()">搜索</el-button>
                </template>
              </el-input>
              <el-button v-if="productsSubMenu === 'list'" type="primary" @click="addProduct">
                <el-icon><Plus /></el-icon>
                <span>添加商品</span>
              </el-button>
            </div>
          </div>
          <el-table :data="products" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="商品名称" />
            <el-table-column prop="category" label="分类" width="120" />
            <el-table-column prop="price" label="价格" width="100">
              <template #default="scope">¥{{ scope.row.price }}</template>
            </el-table-column>
            <el-table-column prop="stock" label="库存" width="80" />
            <el-table-column prop="sellerName" label="卖家" />
            <el-table-column prop="status" label="状态" width="100" v-if="productsSubMenu === 'list'">
              <template #default="scope">
                <el-tag :type="getProductStatusType(scope.row.status)">
                  {{ getProductStatus(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="发布时间" width="180" />
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <template v-if="productsSubMenu === 'pending'">
                  <el-button 
                    size="small" 
                    type="success" 
                    @click="approveProduct(scope.row.id)"
                    title="通过"
                  >
                    <el-icon><Check /></el-icon>
                  </el-button>
                  <el-button 
                    size="small" 
                    type="danger" 
                    @click="rejectProduct(scope.row.id)"
                    title="拒绝"
                  >
                    <el-icon><Close /></el-icon>
                  </el-button>
                </template>
                <template v-else>
                  <el-button 
                    size="small"
                    @click="editProduct(scope.row)"
                    title="编辑"
                  >
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button 
                    size="small" 
                    type="danger" 
                    @click="deleteProduct(scope.row.id)"
                    title="删除"
                  >
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="products.length === 0" class="empty-tip">
            <div class="empty-icon">📦</div>
            <p>{{ productsSubMenu === 'pending' ? '暂无待审核商品' : '暂无商品数据' }}</p>
          </div>
        </div>
        <div v-if="activeMenu === 'orders'" class="panel">
          <div class="panel-header">
            <h3>订单列表</h3>
            <el-input 
              v-model="searchOrder" 
              placeholder="搜索商品名称" 
              class="search-input"
              @keyup.enter="loadOrders()"
            >
              <template #append>
                <el-button @click="loadOrders()">搜索</el-button>
              </template>
            </el-input>
          </div>
          <el-table :data="orders" border>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="productName" label="商品名称" />
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column prop="totalAmount" label="总价" width="100">
              <template #default="scope">¥{{ scope.row.totalAmount }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                  {{ getOrderStatus(scope.row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="160">
              <template #default="scope">
                <el-button 
                  size="small" 
                  icon="Edit" 
                  @click="editOrder(scope.row)"
                  title="编辑状态"
                ></el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="orders.length === 0" class="empty-tip">
            <div class="empty-icon">📋</div>
            <p>暂无订单数据</p>
          </div>
        </div>
      </div>
    </div>
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item v-if="activeMenu === 'users'" label="用户名" required>
          <el-input v-model="formData.username" :disabled="editingId !== null" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item v-if="activeMenu === 'users' && editingId === null" label="密码" required>
          <el-input v-model="formData.password" type="password" placeholder="请输入密码，默认123456" />
        </el-form-item>
        <el-form-item v-if="activeMenu === 'users'" label="手机号" required>
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item v-if="activeMenu === 'users'" label="用户类型">
          <el-select v-model="formData.userType">
            <el-option :value="0" label="普通用户" />
            <el-option :value="1" label="管理员" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="activeMenu === 'products'" label="商品名称">
          <el-input v-model="formData.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item v-if="activeMenu === 'products'" label="分类">
          <el-select v-model="formData.category" placeholder="请选择分类">
            <el-option value="电子产品" label="电子产品" />
            <el-option value="图书教材" label="图书教材" />
            <el-option value="生活用品" label="生活用品" />
            <el-option value="服饰鞋包" label="服饰鞋包" />
            <el-option value="运动器材" label="运动器材" />
            <el-option value="美妆护肤" label="美妆护肤" />
            <el-option value="数码配件" label="数码配件" />
            <el-option value="其他" label="其他" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="activeMenu === 'products'" label="价格">
          <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item v-if="activeMenu === 'products'" label="库存">
          <el-input-number v-model="formData.stock" :min="0" :precision="0" style="width: 100%" />
        </el-form-item>
        <el-form-item v-if="activeMenu === 'products'" label="商品图片">
          <el-input v-model="formData.image" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item v-if="activeMenu === 'products'" label="商品描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入商品描述" />
        </el-form-item>
        <el-form-item v-if="activeMenu === 'orders'" label="订单状态">
          <el-select v-model="formData.status" placeholder="请选择订单状态">
            <el-option :value="0" label="待付款" />
            <el-option :value="1" label="已付款" />
            <el-option :value="2" label="已发货" />
            <el-option :value="3" label="已完成" />
            <el-option :value="4" label="已取消" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveData">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
/**
 * 管理员后台页面 - Admin.vue
 * 功能：提供管理员对用户、商品、订单的管理功能
 * 包含：用户列表管理、商品审核管理、订单状态管理
 */

import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import { useProductStore } from '../stores/product'
import { useOrderStore } from '../stores/order'
import { User, ShoppingBag, Document, Plus, Edit, Delete, Check, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

// 路由实例
const router = useRouter()
// 状态管理实例
const userStore = useUserStore()
const productStore = useProductStore()
const orderStore = useOrderStore()

// 当前激活的菜单
const activeMenu = ref('users')
// 商品管理子菜单状态
const productsSubMenu = ref('list')
// 用户列表数据
const users = ref([])
// 商品列表数据
const products = ref([])
// 订单列表数据
const orders = ref([])
// 用户搜索关键词
const searchUser = ref('')
// 商品搜索关键词
const searchProduct = ref('')
// 订单搜索关键词
const searchOrder = ref('')
// 对话框显示状态
const dialogVisible = ref(false)
// 对话框标题
const dialogTitle = ref('')
// 表单数据
const formData = ref({})
// 正在编辑的记录ID
const editingId = ref(null)

// 当前页面标题
const pageTitle = ref('用户管理')

// 页面标题映射表
const pageTitles = {
  users: '用户管理',
  products: '商品管理',
  orders: '订单管理'
}

/**
 * 处理子菜单点击事件
 * @param {string} menu - 菜单名称（users/products/orders）
 * @param {string} subMenu - 子菜单名称（list/pending）
 */
const handleSubMenuClick = (menu, subMenu = 'list') => {
  activeMenu.value = menu
  productsSubMenu.value = subMenu
  pageTitle.value = pageTitles[menu]
  // 根据菜单类型加载对应数据
  if (menu === 'users') loadUsers()
  else if (menu === 'products') loadProducts()
  else if (menu === 'orders') loadOrders()
}

/**
 * 加载用户列表数据
 * 通过API获取用户列表，支持关键词搜索
 */
const loadUsers = async () => {
  try {
    const token = localStorage.getItem('token')
    const response = await fetch(
      `/api/user/list${searchUser.value ? `?keyword=${encodeURIComponent(searchUser.value)}` : ''}`,
      { headers: { 'Authorization': `Bearer ${token}` } }
    )
    const result = await response.json()
    users.value = result.data || []
  } catch (e) {
    console.error('加载用户列表失败:', e)
    users.value = []
  }
}

/**
 * 加载商品列表数据
 * 根据子菜单状态加载商品列表或待审核商品
 */
const loadProducts = async () => {
  try {
    const token = localStorage.getItem('token')
    let url = '/api/product/list'
    // 如果是待审核子菜单，切换API地址
    if (productsSubMenu.value === 'pending') {
      url = '/api/product/pending'
    }
    // 添加搜索参数
    if (searchProduct.value) {
      url += `?name=${encodeURIComponent(searchProduct.value)}`
    }
    const response = await fetch(url, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const result = await response.json()
    let data = result.data || []
    // 处理分页数据格式
    if (data.records) {
      data = data.records
    }
    products.value = data
  } catch (e) {
    console.error('加载商品列表失败:', e)
    products.value = []
  }
}

/**
 * 加载订单列表数据
 * 通过API获取所有订单，支持商品名称搜索
 */
const loadOrders = async () => {
  try {
    const token = localStorage.getItem('token')
    let url = '/api/order/all'
    // 添加搜索参数
    if (searchOrder.value) {
      url += `?keyword=${encodeURIComponent(searchOrder.value)}`
    }
    const response = await fetch(url, {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const result = await response.json()
    // 后端返回分页对象，需要取records
    const data = result.data || {}
    orders.value = data.records || []
  } catch (e) {
    console.error('加载订单列表失败:', e)
    orders.value = []
  }
}

/**
 * 获取订单状态文本
 * @param {number} status - 订单状态码
 * @returns {string} 状态文本
 */
const getOrderStatus = (status) => {
  const statusMap = {
    0: '待付款',
    1: '已付款',
    2: '已发货',
    3: '已完成',
    4: '已取消'
  }
  return statusMap[status] || '未知'
}

/**
 * 获取订单状态对应的Element标签类型
 * @param {number} status - 订单状态码
 * @returns {string} Element标签类型
 */
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'primary',
    2: 'info',
    3: 'success',
    4: 'danger'
  }
  return typeMap[status] || 'default'
}

/**
 * 获取商品状态文本
 * @param {number} status - 商品状态码
 * @returns {string} 状态文本
 */
const getProductStatus = (status) => {
  const statusMap = {
    0: '待审核',
    1: '已通过',
    2: '已拒绝'
  }
  return statusMap[status] || '未知'
}

/**
 * 获取商品状态对应的Element标签类型
 * @param {number} status - 商品状态码
 * @returns {string} Element标签类型
 */
const getProductStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'danger'
  }
  return typeMap[status] || 'default'
}

/**
 * 打开编辑用户对话框
 * @param {object} row - 用户数据行
 */
const editUser = (row) => {
  editingId.value = row.id
  formData.value = { ...row }
  dialogTitle.value = '编辑用户'
  dialogVisible.value = true
}

/**
 * 打开编辑商品对话框
 * @param {object} row - 商品数据行
 */
const editProduct = (row) => {
  editingId.value = row.id
  formData.value = { ...row }
  dialogTitle.value = '编辑商品'
  dialogVisible.value = true
}

/**
 * 打开添加商品对话框
 */
const addUser = () => {
  editingId.value = null
  formData.value = {
    username: '',
    phone: '',
    userType: 0,
    password: '123456' // 默认密码
  }
  dialogTitle.value = '添加用户'
  dialogVisible.value = true
}

const addProduct = () => {
  editingId.value = null
  formData.value = {
    name: '',
    category: '电子产品',
    price: 0,
    stock: 1,
    image: '',
    description: ''
  }
  dialogTitle.value = '添加商品'
  dialogVisible.value = true
}

/**
 * 打开编辑订单状态对话框
 * @param {object} row - 订单数据行
 */
const editOrder = (row) => {
  editingId.value = row.id
  formData.value = { ...row }
  dialogTitle.value = '编辑订单状态'
  dialogVisible.value = true
}

/**
 * 审核通过商品
 * @param {number} id - 商品ID
 */
const approveProduct = async (id) => {
  if (confirm('确定要通过该商品吗？')) {
    try {
      await productStore.approve(id)
      ElMessage.success('审核通过')
      loadProducts()
    } catch (e) {
      ElMessage.error('操作失败')
    }
  }
}

/**
 * 拒绝商品审核
 * @param {number} id - 商品ID
 */
const rejectProduct = async (id) => {
  if (confirm('确定要拒绝该商品吗？')) {
    try {
      await productStore.reject(id)
      ElMessage.success('已拒绝')
      loadProducts()
    } catch (e) {
      ElMessage.error('操作失败')
    }
  }
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 */
const deleteUser = async (id) => {
  if (confirm('确定要删除该用户吗？')) {
    const token = localStorage.getItem('token')
    try {
      const res = await fetch(`/api/user/${id}`, { 
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.code === 200) {
        ElMessage.success('删除成功')
        loadUsers()
      } else {
        ElMessage.error(data.message || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 删除商品
 * @param {number} id - 商品ID
 */
const deleteProduct = async (id) => {
  if (confirm('确定要删除该商品吗？')) {
    const token = localStorage.getItem('token')
    try {
      const res = await fetch(`/api/product/admin/delete/${id}`, { 
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.code === 200) {
        ElMessage.success('删除成功')
        loadProducts()
      } else {
        ElMessage.error(data.message || '删除失败')
      }
    } catch (e) {
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 保存编辑数据
 * 根据当前激活菜单保存用户/商品/订单数据
 */
const saveData = async () => {
  const token = localStorage.getItem('token')
  try {
    if (activeMenu.value === 'users') {
      if (editingId.value) {
        // 更新用户信息
        const res = await fetch(`/api/user/${editingId.value}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
          body: JSON.stringify(formData.value)
        })
        const data = await res.json()
        if (data.code === 200) {
          ElMessage.success('用户信息更新成功')
          dialogVisible.value = false
          loadUsers()
        } else {
          ElMessage.error(data.message || '更新失败')
        }
      } else {
        // 添加新用户
        const res = await fetch('/api/user/admin/add', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
          body: JSON.stringify(formData.value)
        })
        const data = await res.json()
        if (data.code === 200) {
          ElMessage.success('用户添加成功')
          dialogVisible.value = false
          loadUsers()
        } else {
          ElMessage.error(data.message || '添加失败')
        }
      }
    } else if (activeMenu.value === 'products') {
      // 确保formData包含id
      const payload = { ...formData.value }
      if (editingId.value) {
        payload.id = editingId.value
        // 更新商品信息
        const res = await fetch('/api/product/admin/update', {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
          body: JSON.stringify(payload)
        })
        const data = await res.json()
        if (data.code === 200) {
          ElMessage.success('商品信息更新成功')
          dialogVisible.value = false
          loadProducts()
        } else {
          ElMessage.error(data.message || '更新失败')
        }
      } else {
        // 添加新商品（管理员直接添加，无需审核）
        const res = await fetch('/api/product/admin/add', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${token}` },
          body: JSON.stringify(payload)
        })
        const data = await res.json()
        if (data.code === 200) {
          ElMessage.success('商品添加成功')
          dialogVisible.value = false
          loadProducts()
        } else {
          ElMessage.error(data.message || '添加失败')
        }
      }
    } else if (activeMenu.value === 'orders') {
      // 更新订单状态
      const res = await fetch(`/api/order/${editingId.value}/status?status=${formData.value.status}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      })
      const data = await res.json()
      if (data.code === 200) {
        ElMessage.success('订单状态更新成功')
        dialogVisible.value = false
        loadOrders()
      } else {
        ElMessage.error(data.message || '更新失败')
      }
    }
  } catch (e) {
    ElMessage.error('保存失败')
  }
}

/**
 * 退出登录
 * 清除本地存储并跳转管理员登录页
 */
const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    localStorage.removeItem('userType')
    router.push('/admin/login')
  }
}

/**
 * 组件挂载时初始化数据
 */
onMounted(() => {
  loadUsers()
  loadProducts()
  loadOrders()
})
</script>

<style scoped>
.admin-container {
  display: flex;
  height: 100vh;
  background: #f5f7fa;
  min-height: 100vh;
}

.admin-sidebar {
  width: 250px;
  background: linear-gradient(180deg, #FF6A00 0%, #E55A00 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 100;
}

.sidebar-header {
  padding: 24px 20px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}

.logo {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  margin-bottom: 12px;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.sidebar-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  padding: 10px 0;
  overflow-y: auto;
  background: transparent;
}

.sidebar-menu :deep(.el-menu) {
  border-right: none;
  background: transparent !important;
}

/* 统一设置图标容器大小 */
.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  color: rgba(255, 255, 255, 0.9);
  height: 48px;
  line-height: 48px;
  background: transparent;
  margin: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s ease;
}

/* 统一设置图标尺寸 */
.sidebar-menu :deep(.el-menu-item i),
.sidebar-menu :deep(.el-sub-menu__title i) {
  font-size: 18px !important;
  width: 18px !important;
  height: 18px !important;
  line-height: 18px !important;
  margin-right: 12px !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  flex-shrink: 0;
}

/* 专门针对 menu-icon 类的样式 */
.sidebar-menu :deep(.menu-icon) {
  font-size: 18px !important;
  width: 18px !important;
  height: 18px !important;
  line-height: 18px !important;
  margin-right: 12px !important;
  color: rgba(255, 255, 255, 0.9) !important;
  flex-shrink: 0;
  transition: color 0.2s ease;
}

/* 图标悬停状态 */
.sidebar-menu :deep(.el-sub-menu__title:hover .menu-icon) {
  color: #fff !important;
}

/* 图标激活状态 */
.sidebar-menu :deep(.el-sub-menu.is-active .el-sub-menu__title .menu-icon) {
  color: #fff !important;
}

/* 子菜单图标额外缩进 */
.sidebar-menu :deep(.el-sub-menu .el-menu-item i) {
  font-size: 16px !important;
  width: 16px !important;
  height: 16px !important;
  line-height: 16px !important;
  margin-right: 10px !important;
}

/* 菜单项悬停状态 */
.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item:hover i) {
  color: #fff;
}

/* 菜单项激活状态 */
.sidebar-menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item.is-active i) {
  color: #fff;
}

/* 子菜单标题悬停状态 */
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.sidebar-menu :deep(.el-sub-menu__title:hover i) {
  color: #fff;
}

/* 子菜单激活状态 */
.sidebar-menu :deep(.el-sub-menu.is-active .el-sub-menu__title) {
  background: rgba(255, 255, 255, 0.25);
  color: #fff;
}

.sidebar-menu :deep(.el-sub-menu.is-active .el-sub-menu__title i) {
  color: #fff;
}

/* 子菜单项缩进 */
.sidebar-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 56px !important;
}

/* 子菜单分组背景 */
.sidebar-menu :deep(.el-menu-item-group) {
  background: rgba(0, 0, 0, 0.1) !important;
  margin: 0 8px;
  border-radius: 0 0 6px 6px;
}

/* 子菜单展开箭头 */
.sidebar-menu :deep(.el-sub-menu__icon-arrow) {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px !important;
}

.sidebar-menu :deep(.el-menu--vertical) {
  background: transparent !important;
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

.sidebar-footer :deep(.el-button) {
  width: 100%;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: #fff;
}

.sidebar-footer :deep(.el-button:hover) {
  background: rgba(255, 255, 255, 0.3);
}

.admin-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  min-height: 100vh;
  margin-left: 250px;
}

.content-header {
  padding: 20px 30px;
  background: #fff;
  color: #333;
  border-bottom: 1px solid #eee;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.content-header h1 {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #333;
}

.content-body {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.panel {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.panel-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.panel-header h3::before {
  content: '';
  width: 4px;
  height: 18px;
  background: linear-gradient(180deg, #FF6A00 0%, #FF9500 100%);
  border-radius: 2px;
}

.search-input {
  width: 320px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 8px;
  box-shadow: none;
  border-color: #e8e8e8;
}

.search-input :deep(.el-input__wrapper:hover) {
  border-color: #FF6A00;
}

.search-input :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(255, 106, 0, 0.1);
  border-color: #FF6A00;
}

.search-input :deep(.el-button) {
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
  border: none;
  border-radius: 0 8px 8px 0;
  color: #FFFFFF;
}

.search-input :deep(.el-button:hover) {
  opacity: 0.9;
}

.search-input :deep(.el-button span) {
  color: #FFFFFF;
}

.empty-tip {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-tip .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  color: #ddd;
}

.empty-tip p {
  margin: 0;
  font-size: 14px;
  color: #999;
}

:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table th) {
  background: #fafafa;
  font-weight: 600;
  color: #666;
  font-size: 13px;
  padding: 14px 12px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-table td) {
  padding: 14px 12px;
  font-size: 13px;
  color: #333;
}

:deep(.el-table tr:hover) {
  background: #f9f9f9;
}

:deep(.el-table tr.el-table__row--striped) {
  background: #fafafa;
}

:deep(.el-table tr.el-table__row--striped:hover) {
  background: #f5f5f5;
}

:deep(.el-button--small) {
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 12px;
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  border: none;
}

:deep(.el-button--success:hover) {
  opacity: 0.9;
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
  border: none;
}

:deep(.el-button--danger:hover) {
  opacity: 0.9;
}

:deep(.el-tag) {
  border-radius: 4px;
  padding: 4px 10px;
  font-size: 12px;
}

:deep(.el-dialog) {
  border-radius: 12px;
}

:deep(.el-dialog__header) {
  background: linear-gradient(135deg, #FF6A00 0%, #FF9500 100%);
  border-radius: 12px 12px 0 0;
  padding: 16px 20px;
}

:deep(.el-dialog__title) {
  color: #fff;
  font-weight: 600;
}

:deep(.el-dialog__close) {
  color: rgba(255, 255, 255, 0.8);
}

:deep(.el-dialog__close:hover) {
  color: #fff;
}

:deep(.el-dialog__body) {
  padding: 24px;
}

:deep(.el-dialog__footer) {
  padding: 16px 20px;
  border-top: 1px solid #f0f0f0;
}

@media (max-width: 768px) {
  .admin-sidebar {
    width: 80px;
    padding: 16px 0;
  }
  
  .sidebar-header {
    padding: 16px 0;
  }
  
  .sidebar-header h2 {
    display: none;
  }
  
  .logo {
    width: 48px;
    height: 48px;
  }
  
  .sidebar-menu :deep(.el-menu-item span) {
    display: none;
  }
  
  .sidebar-menu :deep(.el-sub-menu__title span) {
    display: none;
  }
  
  .sidebar-menu :deep(.el-sub-menu .el-menu-item) {
    padding-left: 24px !important;
  }
  
  .sidebar-menu :deep(.el-sub-menu .el-menu-item span) {
    display: inline;
  }
  
  .sidebar-footer :deep(.el-button span) {
    display: none;
  }
  
  .admin-content {
    margin-left: 80px;
  }
  
  .content-header {
    padding: 16px;
  }
  
  .content-header h1 {
    font-size: 18px;
  }
  
  .content-body {
    padding: 16px;
  }
  
  .panel {
    padding: 16px;
  }
  
  .panel-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .search-input {
    width: 100%;
  }
  
  :deep(.el-table) {
    font-size: 12px;
  }
  
  :deep(.el-table th),
  :deep(.el-table td) {
    padding: 10px 8px;
  }
  
  :deep(.el-button--small) {
    padding: 4px 8px;
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .admin-sidebar {
    width: 60px;
  }
  
  .admin-content {
    margin-left: 60px;
  }
  
  .logo {
    width: 40px;
    height: 40px;
  }
}
</style>