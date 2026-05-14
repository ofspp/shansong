<template>
  <div class="layout">
    <header class="header">
      <h1>外卖平台 - 商家端</h1>
      <div class="user-info">
        <span>{{ userStore.userInfo?.username }}</span>
        <button class="btn" @click="logout">退出</button>
      </div>
    </header>
    <div class="main">
      <nav class="sidebar">
        <router-link to="/orders">订单管理</router-link>
        <router-link to="/dishes">菜品管理</router-link>
        <router-link to="/categories">分类管理</router-link>
        <router-link to="/profile">店铺信息</router-link>
      </nav>
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

function logout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}

.header {
  background: #1890ff;
  color: white;
  padding: 0 20px;
  height: 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  font-size: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.main {
  display: flex;
  min-height: calc(100vh - 60px);
}

.sidebar {
  width: 200px;
  background: #fff;
  border-right: 1px solid #f0f0f0;
  padding: 20px 0;
}

.sidebar a {
  display: block;
  padding: 12px 20px;
  color: #333;
  text-decoration: none;
}

.sidebar a:hover,
.sidebar a.router-link-active {
  background: #e6f7ff;
  color: #1890ff;
}

.content {
  flex: 1;
  padding: 20px;
  background: #f5f5f5;
}
</style>
