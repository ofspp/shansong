import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('rider_token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('rider_userInfo') || 'null'))

  function setToken(newToken) {
    token.value = newToken
    localStorage.setItem('rider_token', newToken)
  }

  function setUserInfo(info) {
    userInfo.value = info
    localStorage.setItem('rider_userInfo', JSON.stringify(info))
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('rider_token')
    localStorage.removeItem('rider_userInfo')
  }

  return { token, userInfo, setToken, setUserInfo, logout }
})
