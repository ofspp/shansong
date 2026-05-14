<template>
  <div class="login-container">
    <div class="login-card">
      <h2>商家登录</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label>用户名</label>
          <input v-model="form.username" type="text" placeholder="请输入用户名" required />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input v-model="form.password" type="password" placeholder="请输入密码" required />
        </div>
        <button type="submit" class="btn btn-primary" style="width: 100%">登录</button>
      </form>
      <p v-if="error" class="error">{{ error }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'
import request from '../utils/request'

const router = useRouter()
const userStore = useUserStore()

const form = ref({
  username: '',
  password: ''
})

const error = ref('')

async function handleLogin() {
  try {
    const res = await request.post('/auth/merchant/login', form.value)
    if (res.data.code === 200) {
      userStore.setToken(res.data.data.token)
      userStore.setUserInfo(res.data.data)
      router.push('/')
    } else {
      error.value = res.data.message
    }
  } catch (e) {
    error.value = '登录失败，请检查网络'
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}

.login-card {
  background: white;
  padding: 40px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  width: 400px;
}

.login-card h2 {
  text-align: center;
  margin-bottom: 30px;
}

.error {
  color: #ff4d4f;
  margin-top: 10px;
  text-align: center;
}
</style>
