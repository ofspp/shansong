<template>
  <div class="orders-container">
    <h2>我的订单</h2>
    
    <!-- 订单状态筛选 -->
    <div class="order-filter">
      <el-radio-group v-model="status" @change="loadOrders">
        <el-radio label="">全部</el-radio>
        <el-radio label="PAID">待配送</el-radio>
        <el-radio label="DELIVERING">配送中</el-radio>
        <el-radio label="COMPLETED">已完成</el-radio>
        <el-radio label="CANCELLED">已取消</el-radio>
      </el-radio-group>
    </div>
    
    <!-- 订单列表 -->
    <div class="order-list">
      <el-card
        v-for="order in orders"
        :key="order.id"
        class="order-card"
        @click="navigateToOrderDetail(order.id)"
      >
        <div class="order-header">
          <div class="order-no">订单号: {{ order.orderNo }}</div>
          <div :class="['order-status', order.status.toLowerCase()]">
            {{ getStatusText(order.status) }}
          </div>
        </div>
        <div class="order-merchant">
          <span class="merchant-label">商家:</span>
          <span class="merchant-name">{{ order.merchantName || '未知商家' }}</span>
        </div>
        <div class="order-address">
          <i class="el-icon-location"></i>
          <span>{{ order.receiverName }} {{ order.receiverPhone }}</span>
          <span class="address-detail">{{ order.province }}{{ order.city }}{{ order.district }}{{ order.detailAddress }}</span>
        </div>
        <div class="order-footer">
          <div class="order-time">
            {{ formatTime(order.createTime) }}
          </div>
          <div class="order-amount">
            合计: ¥{{ order.actualAmount }}
          </div>
        </div>
        <div class="order-actions" v-if="order.status === 'PAID'">
          <el-button type="danger" size="small" @click.stop="cancelOrder(order.id)">
            取消订单
          </el-button>
        </div>
        <div class="order-actions" v-else-if="order.status === 'DELIVERING'">
          <el-button type="primary" size="small" @click.stop="confirmOrder(order.id)">
            确认收货
          </el-button>
        </div>
      </el-card>
    </div>
    
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const page = ref(1)
const size = ref(10)
const status = ref('')
const orders = ref([])
const total = ref(0)

const loadOrders = async () => {
  try {
    const response = await request.get('/customer/orders', {
      params: {
        page: page.value,
        size: size.value,
        status: status.value
      }
    })
    
    if (response.data.code === 200) {
      orders.value = response.data.data.records
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取订单列表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('获取订单列表失败:', error)
  }
}

const handleSizeChange = (newSize) => {
  size.value = newSize
  loadOrders()
}

const handleCurrentChange = (newPage) => {
  page.value = newPage
  loadOrders()
}

const navigateToOrderDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

const cancelOrder = async (orderId) => {
  try {
    const response = await request.put(`/customer/order/${orderId}/cancel`)
    
    if (response.data.code === 200) {
      ElMessage.success('订单已取消')
      loadOrders()
    } else {
      ElMessage.error(response.data.message || '取消订单失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('取消订单失败:', error)
  }
}

const confirmOrder = async (orderId) => {
  try {
    const response = await request.put(`/customer/order/${orderId}/confirm`)
    
    if (response.data.code === 200) {
      ElMessage.success('确认收货成功')
      loadOrders()
    } else {
      ElMessage.error(response.data.message || '确认收货失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('确认收货失败:', error)
  }
}

const getStatusText = (status) => {
  const statusMap = {
    'PAID': '待配送',
    'DELIVERING': '配送中',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return statusMap[status] || status
}

const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.orders-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.orders-container h2 {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.order-filter {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.order-list {
  margin-bottom: 20px;
}

.order-card {
  margin-bottom: 15px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.order-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 14px;
  color: #666;
}

.order-status {
  font-size: 14px;
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 12px;
}

.order-status.paid {
  background-color: #e6f7ff;
  color: #1890ff;
}

.order-status.delivering {
  background-color: #f6ffed;
  color: #52c41a;
}

.order-status.completed {
  background-color: #fafafa;
  color: #8c8c8c;
}

.order-status.cancelled {
  background-color: #fff2f0;
  color: #ff4d4f;
}

.order-merchant {
  margin-bottom: 10px;
  font-size: 14px;
}

.merchant-label {
  color: #666;
  margin-right: 5px;
}

.merchant-name {
  font-weight: bold;
}

.order-address {
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
  display: flex;
  align-items: flex-start;
}

.order-address i {
  margin-right: 5px;
  margin-top: 2px;
}

.address-detail {
  margin-left: 5px;
  flex: 1;
  line-height: 1.4;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #f0f0f0;
  font-size: 14px;
  color: #666;
}

.order-amount {
  font-weight: bold;
  color: #ff4d4f;
  font-size: 16px;
}

.order-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
