import { defineStore } from 'pinia'
import axios from '../utils/axios'

export const useOrderStore = defineStore('order', {
  state: () => ({
    buyerOrders: [],
    sellerOrders: [],
    detail: null
  }),
  actions: {
    async create(orderDTO) {
      await axios.post('/order/create', orderDTO)
    },
    async updateStatus(id, status) {
      await axios.put(`/order/status/${id}`, null, { params: { status } })
    },
    async getDetail(id) {
      const data = await axios.get(`/order/detail/${id}`)
      this.detail = data
      return data
    },
    async buyerOrders(page = 1, size = 10) {
      const data = await axios.get('/order/buyer', { params: { page, size } })
      this.buyerOrders = data
      return data
    },
    async sellerOrders(page = 1, size = 10) {
      const data = await axios.get('/order/seller', { params: { page, size } })
      this.sellerOrders = data
      return data
    },
    async listAll() {
      const data = await axios.get('/order/all')
      return data
    }
  }
})