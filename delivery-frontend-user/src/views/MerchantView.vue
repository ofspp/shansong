<template>
  <div class="merchant-container">
    <!-- 商家信息 -->
    <el-card class="merchant-info-card">
      <div class="merchant-header">
        <img :src="merchant.shopLogo" :alt="merchant.shopName" class="merchant-logo" />
        <div class="merchant-details">
          <h2>{{ merchant.shopName }}</h2>
          <div class="merchant-info">
            <span class="delivery-fee">配送费: ¥{{ merchant.deliveryFee }}</span>
            <span class="min-order">起送: ¥{{ merchant.minOrderAmount }}</span>
            <span class="business-hours">{{ merchant.businessHours }}</span>
          </div>
          <div class="merchant-address">
            <i class="el-icon-location"></i>
            {{ merchant.province }}{{ merchant.city }}{{ merchant.district }}{{ merchant.address }}
          </div>
        </div>
      </div>
    </el-card>

    <!-- 分类和菜品 -->
    <div class="menu-container">
      <!-- 分类列表 -->
      <div class="category-list">
        <el-button
          v-for="category in categories"
          :key="category.id"
          :type="activeCategoryId === category.id ? 'primary' : 'default'"
          @click="selectCategory(category.id)"
          class="category-button"
        >
          {{ category.name }}
        </el-button>
      </div>

      <!-- 菜品列表 -->
      <div class="dish-list">
        <el-card
          v-for="dish in dishes"
          :key="dish.id"
          class="dish-card"
        >
          <div class="dish-header">
            <img :src="dish.image" :alt="dish.name" class="dish-image" />
            <div class="dish-info">
              <h3>{{ dish.name }}</h3>
              <p class="dish-description">{{ dish.description }}</p>
              <div class="dish-price">¥{{ dish.price }}</div>
              <div class="dish-sales">月售 {{ dish.sales }}</div>
            </div>
          </div>
          <div class="dish-actions">
            <div class="quantity-control" v-if="getDishQuantity(dish.id) > 0">
              <el-button @click="decreaseQuantity(dish)" size="small">-</el-button>
              <span class="quantity">{{ getDishQuantity(dish.id) }}</span>
              <el-button @click="increaseQuantity(dish)" size="small">+</el-button>
            </div>
            <el-button v-else type="primary" size="small" @click="addToCart(dish)">
              加入购物车
            </el-button>
          </div>
        </el-card>

        <!-- 购物车 -->
        <div class="cart-panel">
          <div class="cart-header">
            <h3>购物车</h3>
            <el-button type="text" @click="clearCart">清空</el-button>
          </div>
          <div class="cart-items" v-if="cartStore.items.length > 0">
            <div v-for="item in cartStore.items" :key="item.id" class="cart-item">
              <div class="cart-item-info">
                <span>{{ item.name }}</span>
                <span class="cart-item-price">¥{{ item.price }} × {{ item.quantity }}</span>
              </div>
              <div class="cart-item-actions">
                <el-button @click="decreaseQuantity(item)" size="small">-</el-button>
                <span class="quantity">{{ item.quantity }}</span>
                <el-button @click="increaseQuantity(item)" size="small">+</el-button>
              </div>
            </div>
            <div class="cart-total">
              <span>合计:</span>
              <span class="total-price">¥{{ cartStore.totalPrice }}</span>
            </div>
            <el-button type="primary" class="checkout-button" @click="goToCheckout">
              去结算
            </el-button>
          </div>
          <div class="empty-cart" v-else>
            购物车为空
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore, useCartStore } from '../stores'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const merchantId = computed(() => route.params.id)
const merchant = ref({})
const categories = ref([])
const dishes = ref([])
const activeCategoryId = ref(null)
const page = ref(1)
const size = ref(20)

const loadMerchantInfo = async () => {
  try {
    const response = await request.get(`/customer/merchant/${merchantId.value}`)
    if (response.data.code === 200) {
      merchant.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('获取商家信息失败')
    console.error('获取商家信息失败:', error)
  }
}

const loadCategories = async () => {
  try {
    const response = await request.get(`/customer/merchant/${merchantId.value}/categories`)
    if (response.data.code === 200) {
      categories.value = response.data.data
      if (categories.value.length > 0) {
        activeCategoryId.value = categories.value[0].id
        loadDishes()
      }
    }
  } catch (error) {
    ElMessage.error('获取分类失败')
    console.error('获取分类失败:', error)
  }
}

const loadDishes = async () => {
  try {
    const response = await request.get(`/customer/merchant/${merchantId.value}/dishes`, {
      params: {
        page: page.value,
        size: size.value,
        categoryId: activeCategoryId.value
      }
    })
    if (response.data.code === 200) {
      dishes.value = response.data.data.records
    }
  } catch (error) {
    ElMessage.error('获取菜品失败')
    console.error('获取菜品失败:', error)
  }
}

const selectCategory = (categoryId) => {
  activeCategoryId.value = categoryId
  loadDishes()
}

const addToCart = (dish) => {
  cartStore.addItem({
    id: dish.id,
    name: dish.name,
    price: dish.price,
    image: dish.image,
    quantity: 1,
    merchantId: merchantId.value
  })
  ElMessage.success('已加入购物车')
}

const increaseQuantity = (item) => {
  cartStore.updateQuantity(item.id, item.quantity + 1)
}

const decreaseQuantity = (item) => {
  cartStore.updateQuantity(item.id, item.quantity - 1)
}

const getDishQuantity = (dishId) => {
  const item = cartStore.items.find(i => i.id === dishId)
  return item ? item.quantity : 0
}

const clearCart = () => {
  cartStore.clearCart()
  ElMessage.success('购物车已清空')
}

const goToCheckout = () => {
  router.push('/order/confirm')
}

onMounted(() => {
  loadMerchantInfo()
  loadCategories()
})
</script>

<style scoped>
.merchant-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.merchant-info-card {
  margin-bottom: 20px;
}

.merchant-header {
  display: flex;
}

.merchant-logo {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  margin-right: 20px;
  object-fit: cover;
}

.merchant-details {
  flex: 1;
}

.merchant-details h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: bold;
}

.merchant-info {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.merchant-address {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #999;
}

.merchant-address i {
  margin-right: 5px;
}

.menu-container {
  display: flex;
  gap: 20px;
}

.category-list {
  width: 200px;
  background-color: #f5f5f5;
  border-radius: 8px;
  padding: 10px;
  height: fit-content;
  position: sticky;
  top: 20px;
}

.category-button {
  width: 100%;
  margin-bottom: 10px;
  text-align: left;
}

.dish-list {
  flex: 1;
  position: relative;
}

.dish-card {
  margin-bottom: 15px;
}

.dish-header {
  display: flex;
  margin-bottom: 10px;
}

.dish-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  margin-right: 15px;
  object-fit: cover;
}

.dish-info {
  flex: 1;
}

.dish-info h3 {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: bold;
}

.dish-description {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.4;
}

.dish-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
  margin-bottom: 5px;
}

.dish-sales {
  font-size: 12px;
  color: #999;
}

.dish-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
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

.cart-panel {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 300px;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  padding: 15px;
  max-height: 400px;
  overflow-y: auto;
}

.cart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.cart-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.cart-items {
  margin-bottom: 15px;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.cart-item-info {
  flex: 1;
  font-size: 14px;
}

.cart-item-price {
  margin-left: 10px;
  color: #666;
}

.cart-item-actions {
  display: flex;
  align-items: center;
  gap: 5px;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
  font-size: 16px;
  font-weight: bold;
}

.total-price {
  color: #ff4d4f;
  font-size: 18px;
}

.checkout-button {
  width: 100%;
  margin-top: 15px;
}

.empty-cart {
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>
