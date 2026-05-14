<template>
  <div class="app-container">
    <!-- 顶部导航栏 -->
    <nav class="top-nav" v-if="!hideNav">
      <div class="nav-content">
        <div class="nav-brand" @click="router.push('/')">🍜 外卖平台</div>
        <div class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/orders" class="nav-link" v-if="userStore.isLoggedIn">我的订单</router-link>
          <router-link to="/cart" class="nav-link" v-if="userStore.isLoggedIn">购物车</router-link>
          <router-link to="/profile" class="nav-link" v-if="userStore.isLoggedIn">个人中心</router-link>
          <router-link to="/login" class="nav-link" v-if="!userStore.isLoggedIn">登录</router-link>
        </div>
      </div>
    </nav>
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from './stores'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const hideNav = computed(() => {
  return route.name === 'login' || route.name === 'register'
})
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.app-container {
  min-height: 100vh;
  font-family: Arial, sans-serif;
}

.top-nav {
  background: linear-gradient(135deg, #409eff, #337ecc);
  color: white;
  padding: 0 20px;
  position: sticky;
  top: 0;
  z-index: 1000;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.nav-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 56px;
}

.nav-brand {
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}

.nav-links {
  display: flex;
  gap: 8px;
}

.nav-link {
  color: rgba(255, 255, 255, 0.85);
  text-decoration: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  transition: all 0.3s;
}

.nav-link:hover {
  background: rgba(255, 255, 255, 0.15);
  color: white;
}

.nav-link.router-link-exact-active {
  background: rgba(255, 255, 255, 0.25);
  color: white;
  font-weight: bold;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
