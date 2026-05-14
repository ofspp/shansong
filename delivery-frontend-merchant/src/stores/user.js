import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('merchant_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('merchant_userInfo') || 'null'))

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('merchant_token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('merchant_userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('merchant_token')
    localStorage.removeItem('merchant_userInfo')
  }

  return { token, userInfo, setToken, setUserInfo, logout }
})
