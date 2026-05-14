<template>
  <div class="available-view">
    <h3>可抢订单</h3>
    <div v-for="order in orders" :key="order.id" class="card order-card">
      <div class="order-header">
        <span>订单号: {{ order.orderNo }}</span>
      </div>
      <div class="order-info">
        <p>收货人: {{ order.receiverName }} {{ order.receiverPhone }}</p>
        <p>地址: {{ order.detailAddress }}</p>
        <p>金额: ¥{{ order.actualAmount }}</p>
      </div>
      <div class="order-actions">
        <button class="btn btn-primary" @click="grabOrder(order.id)">抢单</button>
      </div>
    </div>

    <div v-if="orders.length === 0" class="card">
      <p style="text-align: center; color: #999;">暂无可抢订单</p>
    </div>

    <div class="pagination">
      <button :disabled="page === 1" @click="page--; loadOrders()">上一页</button>
      <span>第 {{ page }} 页</span>
      <button :disabled="orders.length < size" @click="page++; loadOrders()">下一页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const orders = ref([])
const page = ref(1)
const size = ref(10)

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  try {
    const res = await request.get('/rider/available-orders', {
      params: { page: page.value, size: size.value }
    })
    if (res.data.code === 200) {
      orders.value = res.data.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

async function grabOrder(orderId) {
  try {
    const res = await request.post(`/rider/order/${orderId}/grab`)
    if (res.data.code === 200) {
      alert('抢单成功')
      loadOrders()
    } else {
      alert(res.data.message)
    }
  } catch (e) {
    alert('抢单失败')
  }
}
</script>

<style scoped>
h3 {
  margin-bottom: 20px;
}

.order-card {
  margin-bottom: 15px;
}

.order-header {
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.order-info p {
  margin: 5px 0;
  color: #666;
}

.order-actions {
  margin-top: 15px;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}
</style>
