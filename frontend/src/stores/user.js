import { defineStore } from 'pinia'
import { ref } from 'vue'
import axios from '../utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))
  const userType = ref(localStorage.getItem('userType') || '')

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
  }

  function setUserType(type) {
    userType.value = type
    localStorage.setItem('userType', type)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    userType.value = ''
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    localStorage.removeItem('userType')
  }

  async function login(form) {
    const res = await axios.post('/api/user/login', { username: form.username, password: form.password })
    if (res.data.code === 200) {
      setToken(res.data.data.token)
      setUserType(String(res.data.data.userType))
      await fetchUserInfo()
      return true
    }
    throw new Error(res.data.message)
  }

  async function adminLogin(form) {
    const res = await axios.post('/api/user/admin/login', { username: form.username, password: form.password })
    if (res.data.code === 200) {
      setToken(res.data.data.token)
      setUserType('1')
      await fetchUserInfo()
      return true
    }
    throw new Error(res.data.message)
  }

  async function fetchUserInfo() {
    const res = await axios.get('/api/user/info')
    if (res.data.code === 200) {
      setUserInfo(res.data.data)
      return res.data.data
    }
    throw new Error(res.data.message)
  }

  async function update(data) {
    const res = await axios.put('/api/user/update', data)
    if (res.data.code === 200) {
      // 更新本地用户信息
      const newUserInfo = { ...userInfo.value, ...data }
      setUserInfo(newUserInfo)
      return res.data.data
    }
    throw new Error(res.data.message)
  }

  // 根据ID获取用户信息
  async function getUserById(userId) {
    const res = await axios.get(`/api/user/${userId}`)
    return res.data
  }

  const isLoggedIn = () => !!token.value
  const isAdmin = () => userType.value === '1'

  return { token, userInfo, userType, setToken, setUserInfo, setUserType, logout, login, adminLogin, fetchUserInfo, update, getUserById, isLoggedIn, isAdmin }
})
