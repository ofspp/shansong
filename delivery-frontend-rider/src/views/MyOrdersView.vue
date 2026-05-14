<template>
  <div class="my-orders-view">
    <div class="card">
      <div class="filter-bar">
        <select v-model="statusFilter" @change="loadOrders">
          <option value="">全部订单</option>
          <option value="DELIVERING">配送中</option>
          <option value="COMPLETED">已完成</option>
        </select>
      </div>
    </div>

    <div v-for="order in orders" :key="order.id" class="card order-card">
      <div class="order-header">
        <span>订单号: {{ order.orderNo }}</span>
        <span class="status-tag" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
      </div>
      <div class="order-info">
        <p>收货人: {{ order.receiverName }} {{ order.receiverPhone }}</p>
        <p>地址: {{ order.detailAddress }}</p>
        <p>金额: ¥{{ order.actualAmount }}</p>
      </div>
      <div class="order-actions">
        <button v-if="order.status === 'DELIVERING'" class="btn btn-success" @click="completeOrder(order.id)">确认送达</button>
      </div>
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
const statusFilter = ref('')
const page = ref(1)
const size = ref(10)

onMounted(() => {
  loadOrders()
})

async function loadOrders() {
  try {
    const res = await request.get('/rider/my-orders', {
      params: { page: page.value, size: size.value, status: statusFilter.value }
    })
    if (res.data.code === 200) {
      orders.value = res.data.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

async function completeOrder(orderId) {
  if (!confirm('确认已送达？')) return
  try {
    const res = await request.put(`/rider/order/${orderId}/status`, null, {
      params: { status: 'COMPLETED' }
    })
    if (res.data.code === 200) {
      loadOrders()
    } else {
      alert(res.data.message)
    }
  } catch (e) {
    alert('操作失败')
  }
}

function getStatusText(status) {
  const map = {
    DELIVERING: '配送中',
    COMPLETED: '已完成'
  }
  return map[status] || status
}

function getStatusClass(status) {
  const map = {
    DELIVERING: 'status-warning',
    COMPLETED: 'status-success'
  }
  return map[status] || ''
}
</script>

<style scoped>
.filter-bar {
  margin-bottom: 20px;
}

.filter-bar select {
  width: 200px;
}

.order-card {
  margin-bottom: 15px;
}

.order-header {
  display: flex;
  justify-content: space-between;
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
