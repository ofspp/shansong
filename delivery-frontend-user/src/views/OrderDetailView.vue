<template>
  <div class="order-detail-container">
    <h2>订单详情</h2>
    
    <el-card v-if="order" class="order-card">
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
        <h3>收货信息</h3>
        <div class="address-content">
          <div class="receiver-info">
            <span class="receiver-name">{{ order.receiverName }}</span>
            <span class="receiver-phone">{{ order.receiverPhone }}</span>
          </div>
          <div class="address-detail">
            {{ order.province }}{{ order.city }}{{ order.district }}{{ order.detailAddress }}
          </div>
        </div>
      </div>
      
      <div class="order-items">
        <h3>订单商品</h3>
        <div v-for="item in orderItems" :key="item.id" class="order-item">
          <img :src="item.dishImage" :alt="item.dishName" class="item-image" />
          <div class="item-info">
            <h4>{{ item.dishName }}</h4>
            <div class="item-price">¥{{ item.price }} × {{ item.quantity }}</div>
            <div class="item-subtotal">小计: ¥{{ item.subtotal }}</div>
          </div>
        </div>
      </div>
      
      <div class="order-amount">
        <h3>订单金额</h3>
        <div class="amount-item">
          <span>商品总价:</span>
          <span>¥{{ order.totalAmount }}</span>
        </div>
        <div class="amount-item">
          <span>配送费:</span>
          <span>¥{{ order.deliveryFee }}</span>
        </div>
        <div class="amount-total">
          <span>实付金额:</span>
          <span>¥{{ order.actualAmount }}</span>
        </div>
      </div>
      
      <div class="order-time">
        <h3>订单时间</h3>
        <div class="time-item">
          <span>创建时间:</span>
          <span>{{ formatTime(order.createTime) }}</span>
        </div>
      </div>
      
      <div class="order-actions" v-if="order.status === 'PENDING_PAYMENT'">
        <el-button type="primary" @click="showPayDialog = true">去支付 ¥{{ order.actualAmount }}</el-button>
        <el-button type="danger" @click="cancelOrder">取消订单</el-button>
      </div>
      
      <div class="order-actions" v-else-if="order.status === 'DELIVERING'">
        <el-button type="primary" @click="confirmOrder">确认收货</el-button>
      </div>
      
      <div class="order-actions" v-else-if="order.status === 'COMPLETED' && !evaluation">
        <el-button type="primary" @click="goToEvaluate">去评价</el-button>
      </div>
    </el-card>
    
    <el-empty v-else description="订单不存在" />
    
    <!-- 模拟支付对话框 -->
    <el-dialog v-model="showPayDialog" title="模拟支付" width="400px" :close-on-click-modal="false">
      <div class="pay-dialog-content">
        <div class="pay-amount-display">
          <span>应付金额</span>
          <span class="pay-amount-value">¥{{ order?.actualAmount }}</span>
        </div>
        <el-form @submit.prevent="handlePay">
          <el-form-item label="请输入支付金额">
            <el-input v-model="payAmount" type="number" placeholder="请输入与应付金额一致的数字" step="0.01" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <el-button @click="showPayDialog = false">取消</el-button>
        <el-button type="primary" @click="handlePay" :loading="paying">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const orderId = ref(route.params.id)
const order = ref(null)
const orderItems = ref([])
const evaluation = ref(null)
const showPayDialog = ref(false)
const payAmount = ref('')
const paying = ref(false)

const loadOrderDetail = async () => {
  try {
    const response = await request.get(`/customer/order/${orderId.value}`)
    
    if (response.data.code === 200) {
      order.value = response.data.data
      // 从后端API加载订单明细
      loadOrderItems()
      // 检查是否已经评价
      loadEvaluation()
    } else {
      ElMessage.error(response.data.message || '获取订单详情失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('获取订单详情失败:', error)
  }
}

const loadOrderItems = async () => {
  try {
    const response = await request.get(`/customer/order/${orderId.value}/items`)
    if (response.data.code === 200) {
      orderItems.value = response.data.data
    }
  } catch (error) {
    console.error('获取订单明细失败:', error)
  }
}

const loadEvaluation = async () => {
  try {
    const response = await request.get(`/evaluation/order/${orderId.value}`)
    
    if (response.data.code === 200 && response.data.data) {
      evaluation.value = response.data.data
    }
  } catch (error) {
    console.error('获取评价失败:', error)
  }
}

const handlePay = async () => {
  if (!payAmount.value) {
    ElMessage.warning('请输入支付金额')
    return
  }
  paying.value = true
  try {
    const response = await request.put(`/customer/order/${orderId.value}/pay`, null, {
      params: { amount: payAmount.value }
    })
    if (response.data.code === 200) {
      ElMessage.success('支付成功！')
      showPayDialog.value = false
      payAmount.value = ''
      loadOrderDetail()
    } else {
      ElMessage.error(response.data.message || '支付失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('支付失败:', error)
  } finally {
    paying.value = false
  }
}

const cancelOrder = async () => {
  try {
    const response = await request.put(`/customer/order/${orderId.value}/cancel`)
    
    if (response.data.code === 200) {
      ElMessage.success('订单已取消')
      loadOrderDetail()
    } else {
      ElMessage.error(response.data.message || '取消订单失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('取消订单失败:', error)
  }
}

const confirmOrder = async () => {
  try {
    const response = await request.put(`/customer/order/${orderId.value}/confirm`)
    
    if (response.data.code === 200) {
      ElMessage.success('确认收货成功')
      loadOrderDetail()
    } else {
      ElMessage.error(response.data.message || '确认收货失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('确认收货失败:', error)
  }
}

const goToEvaluate = () => {
  router.push(`/evaluation/${orderId.value}`)
}

const getStatusText = (status) => {
  const statusMap = {
    'PENDING_PAYMENT': '待支付',
    'PAID': '已支付',
    'PENDING_ACCEPT': '待接单',
    'REJECTED': '已拒绝',
    'ACCEPTED': '已接单',
    'PICKING': '取餐中',
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
  loadOrderDetail()
})
</script>

<style scoped>
.order-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.order-detail-container h2 {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.order-card {
  margin-bottom: 20px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.order-no {
  font-size: 16px;
  color: #666;
}

.order-status {
  font-size: 16px;
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 16px;
}

.order-status.pending_payment {
  background-color: #fff7e6;
  color: #fa8c16;
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
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 16px;
}

.merchant-label {
  color: #666;
  margin-right: 10px;
}

.merchant-name {
  font-weight: bold;
}

.order-address,
.order-items,
.order-amount,
.order-time {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.order-address h3,
.order-items h3,
.order-amount h3,
.order-time h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: bold;
}

.address-content {
  margin-left: 20px;
}

.receiver-info {
  margin-bottom: 10px;
  font-weight: bold;
}

.receiver-name {
  margin-right: 20px;
}

.address-detail {
  color: #666;
  line-height: 1.4;
}

.order-item {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  margin-right: 15px;
  object-fit: cover;
  background-color: #f5f5f5;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-info h4 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: bold;
}

.item-price {
  font-size: 14px;
  color: #666;
  margin-bottom: 5px;
}

.item-subtotal {
  font-size: 14px;
  font-weight: bold;
  color: #ff4d4f;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.amount-total {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
  font-size: 16px;
  font-weight: bold;
}

.amount-total span:last-child {
  color: #ff4d4f;
  font-size: 18px;
}

.time-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  color: #666;
}

.order-actions {
  margin-top: 20px;
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.pay-dialog-content {
  padding: 10px 0;
}

.pay-amount-display {
  text-align: center;
  margin-bottom: 30px;
}

.pay-amount-display span:first-child {
  display: block;
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.pay-amount-value {
  font-size: 36px;
  font-weight: bold;
  color: #ff4d4f;
}
</style>
