<template>
  <div class="cart-container">
    <h2>购物车</h2>
    
    <div v-if="cartStore.items.length > 0">
      <el-card v-for="item in cartStore.items" :key="item.id" class="cart-item-card">
        <div class="cart-item">
          <img :src="item.image" :alt="item.name" class="item-image" />
          <div class="item-info">
            <h3>{{ item.name }}</h3>
            <div class="item-price">¥{{ item.price }}</div>
          </div>
          <div class="item-actions">
            <div class="quantity-control">
              <el-button @click="decreaseQuantity(item)" size="small">-</el-button>
              <span class="quantity">{{ item.quantity }}</span>
              <el-button @click="increaseQuantity(item)" size="small">+</el-button>
            </div>
            <el-button type="danger" size="small" @click="removeItem(item.id)">删除</el-button>
          </div>
        </div>
      </el-card>
      
      <div class="cart-summary">
        <div class="summary-item">
          <span>商品总价:</span>
          <span>¥{{ cartStore.totalPrice }}</span>
        </div>
        <div class="summary-item">
          <span>配送费:</span>
          <span>¥{{ deliveryFee }}</span>
        </div>
        <div class="summary-total">
          <span>合计:</span>
          <span>¥{{ totalAmount }}</span>
        </div>
        <el-button type="primary" class="checkout-button" @click="goToCheckout">
          去结算
        </el-button>
      </div>
    </div>
    
    <div v-else class="empty-cart">
      <el-empty description="购物车为空" />
      <el-button type="primary" @click="goToHome">去购物</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../stores'

const router = useRouter()
const cartStore = useCartStore()

const deliveryFee = ref(5)

const totalAmount = computed(() => {
  return (cartStore.totalPrice + deliveryFee.value).toFixed(2)
})

const increaseQuantity = (item) => {
  cartStore.updateQuantity(item.id, item.quantity + 1)
}

const decreaseQuantity = (item) => {
  cartStore.updateQuantity(item.id, item.quantity - 1)
}

const removeItem = (itemId) => {
  cartStore.removeItem(itemId)
}

const goToCheckout = () => {
  router.push('/order/confirm')
}

const goToHome = () => {
  router.push('/')
}
</script>

<style scoped>
.cart-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.cart-container h2 {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.cart-item-card {
  margin-bottom: 15px;
}

.cart-item {
  display: flex;
  align-items: center;
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  margin-right: 20px;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: bold;
}

.item-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.item-actions {
  display: flex;
  align-items: center;
  gap: 20px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity {
  min-width: 30px;
  text-align: center;
}

.cart-summary {
  margin-top: 30px;
  padding: 20px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 16px;
}

.summary-total {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ddd;
  font-size: 18px;
  font-weight: bold;
}

.checkout-button {
  width: 100%;
  margin-top: 20px;
  font-size: 16px;
  padding: 10px;
}

.empty-cart {
  text-align: center;
  padding: 60px 20px;
}

.empty-cart .el-button {
  margin-top: 20px;
}
</style>
