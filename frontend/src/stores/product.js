import { defineStore } from 'pinia'
import axios from '../utils/axios'

export const useProductStore = defineStore('product', {
  state: () => ({
    products: [],
    hotProducts: [],
    myProducts: [],
    detail: null
  }),
  actions: {
    async add(productDTO) {
      await axios.post('/product/add', productDTO)
    },
    async update(productDTO) {
      await axios.put('/product/update', productDTO)
    },
    async delete(id) {
      await axios.delete(`/product/delete/${id}`)
    },
    async getDetail(id) {
      const data = await axios.get(`/product/detail/${id}`)
      this.detail = data
      return data
    },
    async list(params) {
      const data = await axios.get('/product/list', { params })
      this.products = data
      return data
    },
    async hot() {
      const data = await axios.get('/product/hot')
      this.hotProducts = data
      return data
    },
    async my() {
      const data = await axios.get('/product/my')
      this.myProducts = data
      return data
    }
  }
})