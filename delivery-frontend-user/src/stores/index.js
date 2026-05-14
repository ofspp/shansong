import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: (() => { try { return JSON.parse(localStorage.getItem('userInfo')) || null } catch { return null } })()
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setUserInfo(userInfo) {
      this.userInfo = userInfo
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
    },
    logout() {
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
    }
  }
})

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: (() => { try { return JSON.parse(localStorage.getItem('cart')) || [] } catch { return [] } })()
  }),
  getters: {
    totalItems: (state) => state.items.length,
    totalPrice: (state) => {
      return state.items.reduce((total, item) => {
        return total + (item.price * item.quantity)
      }, 0)
    }
  },
  actions: {
    addItem(item) {
      const existingItem = this.items.find(i => i.id === item.id)
      if (existingItem) {
        existingItem.quantity += item.quantity
      } else {
        this.items.push(item)
      }
      this.saveCart()
    },
    updateQuantity(itemId, quantity) {
      const item = this.items.find(i => i.id === itemId)
      if (item) {
        item.quantity = quantity
        if (quantity <= 0) {
          this.removeItem(itemId)
        }
        this.saveCart()
      }
    },
    removeItem(itemId) {
      this.items = this.items.filter(i => i.id !== itemId)
      this.saveCart()
    },
    clearCart() {
      this.items = []
      this.saveCart()
    },
    saveCart() {
      localStorage.setItem('cart', JSON.stringify(this.items))
    }
  }
})

export const useOrderStore = defineStore('order', {
  state: () => ({
    orders: []
  }),
  actions: {
    setOrders(orders) {
      this.orders = orders
    },
    addOrder(order) {
      this.orders.unshift(order)
    }
  }
})