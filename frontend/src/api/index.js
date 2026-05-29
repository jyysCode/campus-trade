import request from '../utils/request'

// 商品
export const getHotProducts = () => request.get('/api/product/hot')
export const getProductList = (params) => request.get('/api/product/list', { params })
export const getProductDetail = (id) => request.get(`/api/product/detail/${id}`)
export const searchProducts = (params) => request.get('/api/product/search', { params })
export const getCategories = () => request.get('/api/product/categories')
export const addProduct = (data) => request.post('/api/product/add', data)
export const updateProduct = (data) => request.put('/api/product/update', data)
export const deleteProduct = (id) => request.delete(`/api/product/delete/${id}`)
export const getMyProducts = (params) => request.get('/api/product/my', { params })
export const approveProduct = (id) => request.post(`/api/product/approve/${id}`)
export const rejectProduct = (id) => request.post(`/api/product/reject/${id}`)
export const getPendingProducts = (params) => request.get('/api/product/pending', { params })

// 用户
export const register = (data) => request.post('/api/user/register', data)
export const resetPassword = (data) => request.post('/api/user/reset-password', data)
export const updateUserInfo = (data) => request.put('/api/user/update', data)
export const getUserList = (params) => request.get('/api/user/list', { params })
export const deleteUser = (id) => request.delete(`/api/user/${id}`)
export const updateUserAdmin = (id, data) => request.put(`/api/user/${id}`, data)

// 订单
export const createOrder = (data) => request.post('/api/order/create', data)
export const updateOrderStatus = (id, status) => request.put(`/api/order/status/${id}`, null, { params: { status } })
export const getOrderDetail = (id) => request.get(`/api/order/detail/${id}`)
export const getBuyerOrders = (params) => request.get('/api/order/buyer', { params })
export const getSellerOrders = (params) => request.get('/api/order/seller', { params })
export const getAllOrders = (params) => request.get('/api/order/all', { params })
export const adminUpdateOrder = (id, data) => request.put(`/api/order/${id}/status`, data)

// 收藏
export const addFavorite = (productId) => request.post(`/api/favorite/add/${productId}`)
export const removeFavorite = (productId) => request.delete(`/api/favorite/remove/${productId}`)
export const checkFavorite = (productId) => request.get(`/api/favorite/check/${productId}`)
export const getFavoriteList = (params) => request.get('/api/favorite/list', { params })
export const getFavoriteProductIds = () => request.get('/api/favorite/product-ids')

// 购物车
export const addToCart = (productId) => request.post(`/api/cart/add/${productId}`)
export const updateCartQuantity = (productId, quantity) => request.put(`/api/cart/update/${productId}`, null, { params: { quantity } })
export const removeFromCart = (productId) => request.delete(`/api/cart/remove/${productId}`)
export const clearCart = () => request.delete('/api/cart/clear')
export const getCartList = () => request.get('/api/cart/list')
export const getCartCount = () => request.get('/api/cart/count')

// 评价
export const addReview = (data) => request.post('/api/review/add', null, { params: data })
export const getProductReviews = (productId, params) => request.get(`/api/review/product/${productId}`, { params })
export const getOrderReview = (orderId) => request.get(`/api/review/order/${orderId}`)

// 消息
export const sendMessage = (data) => request.post('/api/message/send', data)
export const getConversation = (userId, otherUserId) => request.get('/api/message/conversation', { params: { userId, otherUserId } })

// 文件上传
export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/api/upload', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
}

// 验证码
export const getCaptchaUrl = () => `/api/captcha?t=${Date.now()}`
