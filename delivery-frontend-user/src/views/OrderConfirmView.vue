<template>
  <div class="order-confirm-container">
    <h2>确认订单</h2>
    
    <!-- 收货地址 -->
    <el-card class="address-card">
      <template #header>
        <div class="card-header">
          <h3>收货地址</h3>
          <el-button type="primary" size="small" @click="openAddressDialog">添加地址</el-button>
        </div>
      </template>
      <div class="address-list">
        <el-radio-group v-model="selectedAddressId">
          <el-radio
            v-for="address in addresses"
            :key="address.id"
            :label="address.id"
            class="address-item"
          >
            <div class="address-content">
              <div class="address-header">
                <span class="receiver-name">{{ address.receiverName }}</span>
                <span class="receiver-phone">{{ address.receiverPhone }}</span>
              </div>
              <div class="address-detail">
                {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detailAddress }}
              </div>
            </div>
          </el-radio>
        </el-radio-group>
      </div>
    </el-card>
    
    <!-- 订单商品 -->
    <el-card class="order-items-card">
      <template #header>
        <h3>订单商品</h3>
      </template>
      <div class="order-items">
        <div v-for="item in cartStore.items" :key="item.id" class="order-item">
          <img :src="item.image" :alt="item.name" class="item-image" />
          <div class="item-info">
            <h4>{{ item.name }}</h4>
            <div class="item-price">¥{{ item.price }} × {{ item.quantity }}</div>
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 订单信息 -->
    <el-card class="order-info-card">
      <template #header>
        <h3>订单信息</h3>
      </template>
      <div class="order-info">
        <el-form :model="orderForm" label-width="100px">
          <el-form-item label="商家">
            <span>{{ merchantName }}</span>
          </el-form-item>
          <el-form-item label="配送费">
            <span>¥{{ deliveryFee }}</span>
          </el-form-item>
          <el-form-item label="商品总价">
            <span>¥{{ cartStore.totalPrice }}</span>
          </el-form-item>
          <el-form-item label="合计">
            <span class="total-amount">¥{{ totalAmount }}</span>
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="orderForm.remark" placeholder="请输入备注" />
          </el-form-item>
        </el-form>
      </div>
    </el-card>
    
    <!-- 提交订单 -->
    <div class="submit-order">
      <div class="order-summary">
        <span>合计:</span>
        <span class="summary-total">¥{{ totalAmount }}</span>
      </div>
      <el-button type="primary" class="submit-button" @click="submitOrder" :loading="loading">
        提交订单
      </el-button>
    </div>

    <!-- 添加地址弹窗 -->
    <el-dialog v-model="addressDialogVisible" title="新增收货地址" width="500px" destroy-on-close>
      <el-form :model="addressForm" :rules="addressRules" ref="addressFormRef" label-width="90px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="addressForm.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="addressForm.receiverPhone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="addressForm.province" placeholder="如：河北省" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="addressForm.city" placeholder="如：唐山市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="addressForm.district" placeholder="如：路北区" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input v-model="addressForm.detailAddress" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="addressForm.isDefault" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="addressSaving" @click="saveNewAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore, useCartStore } from '../stores'
import request from '../utils/request'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const cartStore = useCartStore()

const selectedAddressId = ref(null)
const addresses = ref([])
const merchantName = ref('示例商家')
const deliveryFee = ref(5)
const loading = ref(false)

const orderForm = ref({
  remark: ''
})

const addressDialogVisible = ref(false)
const addressSaving = ref(false)
const addressFormRef = ref(null)

const addressForm = ref({
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

const addressRules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const totalAmount = computed(() => {
  return (cartStore.totalPrice + deliveryFee.value).toFixed(2)
})

const loadAddresses = async () => {
  try {
    const response = await request.get('/customer/addresses')
    if (response.data.code === 200) {
      addresses.value = response.data.data || []
      if (addresses.value.length > 0) {
        // 优先选择默认地址
        const defaultAddr = addresses.value.find(a => a.isDefault === 1)
        selectedAddressId.value = defaultAddr ? defaultAddr.id : addresses.value[0].id
      }
    } else {
      ElMessage.error('获取地址失败')
    }
  } catch (error) {
    ElMessage.error('获取地址失败')
    console.error('获取地址失败:', error)
  }
}

const openAddressDialog = () => {
  addressForm.value = {
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: 0
  }
  addressDialogVisible.value = true
}

const saveNewAddress = async () => {
  if (!addressFormRef.value) return
  await addressFormRef.value.validate()
  addressSaving.value = true
  try {
    const res = await request.post('/customer/address', addressForm.value)
    if (res.data.code === 200) {
      ElMessage.success('地址添加成功')
      addressDialogVisible.value = false
      await loadAddresses()
      // 自动选中新添加的地址
      if (res.data.data && res.data.data.id) {
        selectedAddressId.value = res.data.data.id
      }
    } else {
      ElMessage.error(res.data.message || '添加失败')
    }
  } catch (e) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    addressSaving.value = false
  }
}

// 将前端购物车同步到后端
const syncCartToBackend = async () => {
  // 先清空后端购物车
  await request.delete('/customer/cart')
  // 逐个添加商品到后端购物车
  for (const item of cartStore.items) {
    await request.post('/customer/cart', null, {
      params: {
        dishId: item.id,
        quantity: item.quantity
      }
    })
  }
}

const submitOrder = async () => {
  if (!selectedAddressId.value) {
    ElMessage.error('请选择收货地址')
    return
  }
  
  if (cartStore.items.length === 0) {
    ElMessage.error('购物车为空')
    return
  }
  
  loading.value = true
  try {
    // 先将购物车同步到后端
    await syncCartToBackend()
    
    const response = await request.post('/customer/order', null, {
      params: {
        addressId: selectedAddressId.value,
        remark: orderForm.value.remark
      }
    })
    
    if (response.data.code === 200) {
      ElMessage.success('订单创建成功')
      // 清空购物车
      cartStore.clearCart()
      // 跳转到订单详情
      router.push(`/order/${response.data.data}`)
    } else {
      ElMessage.error(response.data.message || '订单创建失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('订单创建失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAddresses()
  
  // 如果购物车为空，跳回首页
  if (cartStore.items.length === 0) {
    ElMessage.warning('购物车为空')
    router.push('/')
  }
})
</script>

<style scoped>
.order-confirm-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.order-confirm-container h2 {
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: bold;
}

.address-card,
.order-items-card,
.order-info-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: bold;
}

.address-list {
  margin-top: 15px;
}

.address-item {
  display: block;
  margin-bottom: 10px;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.address-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.address-content {
  width: 100%;
}

.address-header {
  display: flex;
  gap: 20px;
  margin-bottom: 10px;
  font-weight: bold;
}

.address-detail {
  color: #666;
  line-height: 1.4;
}

.order-items {
  margin-top: 15px;
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
  width: 80px;
  height: 80px;
  border-radius: 8px;
  margin-right: 15px;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-info h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: bold;
}

.item-price {
  font-size: 14px;
  color: #666;
}

.order-info {
  margin-top: 15px;
}

.total-amount {
  font-size: 18px;
  font-weight: bold;
  color: #ff4d4f;
}

.submit-order {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: white;
  border-top: 1px solid #e4e7ed;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.order-summary {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
}

.summary-total {
  font-size: 20px;
  font-weight: bold;
  color: #ff4d4f;
}

.submit-button {
  font-size: 16px;
  padding: 10px 30px;
}
</style>
