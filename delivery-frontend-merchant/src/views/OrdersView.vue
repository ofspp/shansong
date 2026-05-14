<template>
  <div class="orders-view">
    <div class="card">
      <div class="filter-bar">
        <select v-model="statusFilter" @change="loadOrders">
          <option value="">全部订单</option>
          <option value="PENDING_ACCEPT">待接单</option>
          <option value="ACCEPTED">已接单</option>
          <option value="PICKING">备餐中</option>
          <option value="DELIVERING">配送中</option>
          <option value="COMPLETED">已完成</option>
          <option value="CANCELLED">已取消</option>
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
        <p>备注: {{ order.remark || '无' }}</p>
      </div>
      <div class="order-items">
        <div v-for="item in order.items" :key="item.id" class="order-item">
          {{ item.dishName }} x {{ item.quantity }} = ¥{{ item.subtotal }}
        </div>
      </div>
      <div class="order-actions">
        <button v-if="order.status === 'PENDING_ACCEPT'" class="btn btn-primary" @click="acceptOrder(order.id)">接单</button>
        <button v-if="order.status === 'PENDING_ACCEPT'" class="btn btn-danger" @click="rejectOrder(order.id)">拒单</button>
        <button v-if="order.status === 'ACCEPTED'" class="btn btn-success" @click="startPreparing(order.id)">开始备餐</button>
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
    const res = await request.get('/merchant/orders', {
      params: { page: page.value, size: size.value, status: statusFilter.value }
    })
    if (res.data.code === 200) {
      orders.value = res.data.data.records
      for (let order of orders.value) {
        const itemsRes = await request.get(`/merchant/order/${order.id}/items`)
        if (itemsRes.data.code === 200) {
          order.items = itemsRes.data.data
        }
      }
    }
  } catch (e) {
    console.error(e)
  }
}

async function acceptOrder(orderId) {
  try {
    const res = await request.put(`/merchant/order/${orderId}/accept`)
    if (res.data.code === 200) {
      loadOrders()
    } else {
      alert(res.data.message)
    }
  } catch (e) {
    alert('操作失败')
  }
}

async function rejectOrder(orderId) {
  if (!confirm('确定要拒绝此订单吗？')) return
  try {
    const res = await request.put(`/merchant/order/${orderId}/reject`)
    if (res.data.code === 200) {
      loadOrders()
    } else {
      alert(res.data.message)
    }
  } catch (e) {
    alert('操作失败')
  }
}

async function startPreparing(orderId) {
  try {
    const res = await request.put(`/merchant/order/${orderId}/prepare`)
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
    PENDING_PAYMENT: '待支付',
    PAID: '已支付',
    PENDING_ACCEPT: '待接单',
    REJECTED: '已拒绝',
    ACCEPTED: '已接单',
    PICKING: '备餐中',
    DELIVERING: '配送中',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return map[status] || status
}

function getStatusClass(status) {
  const map = {
    PENDING_ACCEPT: 'status-warning',
    ACCEPTED: 'status-success',
    PICKING: 'status-success',
    DELIVERING: 'status-success',
    COMPLETED: 'status-success',
    CANCELLED: 'status-error',
    REJECTED: 'status-error'
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

.order-items {
  margin: 10px 0;
  padding: 10px;
  background: #fafafa;
  border-radius: 4px;
}

.order-item {
  padding: 5px 0;
}

.order-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}
</style>
