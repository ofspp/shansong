<template>
  <div class="profile-container">
    <!-- 顶部用户信息 -->
    <el-card class="user-card">
      <div class="user-info">
        <el-avatar :size="64" icon="UserFilled" />
        <div class="user-detail">
          <h2>{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</h2>
          <p>{{ userStore.userInfo?.phone || '未设置手机号' }}</p>
        </div>
        <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
      </div>
    </el-card>

    <!-- 快捷入口 -->
    <el-card class="shortcut-card">
      <div class="shortcut-list">
        <div class="shortcut-item" @click="router.push('/')">
          <el-icon :size="28"><HomeFilled /></el-icon>
          <span>首页</span>
        </div>
        <div class="shortcut-item" @click="router.push('/orders')">
          <el-icon :size="28"><List /></el-icon>
          <span>我的订单</span>
        </div>
        <div class="shortcut-item" @click="router.push('/cart')">
          <el-icon :size="28"><ShoppingCart /></el-icon>
          <span>购物车</span>
        </div>
      </div>
    </el-card>

    <!-- 收货地址管理 -->
    <el-card class="address-card">
      <template #header>
        <div class="card-header">
          <h3>收货地址管理</h3>
          <el-button type="primary" @click="openAddDialog">新增地址</el-button>
        </div>
      </template>
      <div v-if="addresses.length === 0" class="empty-tip">
        <el-empty description="暂无收货地址，请添加" />
      </div>
      <div v-else class="address-list">
        <div v-for="addr in addresses" :key="addr.id" class="address-item">
          <div class="address-main">
            <div class="address-top">
              <span class="receiver-name">{{ addr.receiverName }}</span>
              <span class="receiver-phone">{{ addr.receiverPhone }}</span>
              <el-tag v-if="addr.isDefault === 1" type="success" size="small">默认</el-tag>
            </div>
            <div class="address-detail">
              {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detailAddress }}
            </div>
          </div>
          <div class="address-actions">
            <el-button v-if="addr.isDefault !== 1" text type="primary" size="small" @click="setDefault(addr.id)">设为默认</el-button>
            <el-button text type="primary" size="small" @click="openEditDialog(addr)">编辑</el-button>
            <el-button text type="danger" size="small" @click="deleteAddr(addr.id)">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 新增/编辑地址弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑地址' : '新增地址'" width="500px" destroy-on-close>
      <el-form :model="addressForm" :rules="rules" ref="formRef" label-width="90px">
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
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveAddress">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores'
import request from '../utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, List, ShoppingCart } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const addresses = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const formRef = ref(null)

const addressForm = ref({
  id: null,
  receiverName: '',
  receiverPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: 0
})

const rules = {
  receiverName: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  receiverPhone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detailAddress: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

const loadAddresses = async () => {
  try {
    const res = await request.get('/customer/addresses')
    if (res.data.code === 200) {
      addresses.value = res.data.data || []
    }
  } catch (e) {
    ElMessage.error('获取地址列表失败')
  }
}

const openAddDialog = () => {
  isEdit.value = false
  addressForm.value = {
    id: null,
    receiverName: '',
    receiverPhone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: 0
  }
  dialogVisible.value = true
}

const openEditDialog = (addr) => {
  isEdit.value = true
  addressForm.value = { ...addr }
  dialogVisible.value = true
}

const saveAddress = async () => {
  if (!formRef.value) return
  await formRef.value.validate()
  saving.value = true
  try {
    if (isEdit.value) {
      const res = await request.put('/customer/address', addressForm.value)
      if (res.data.code === 200) {
        ElMessage.success('修改成功')
      } else {
        ElMessage.error(res.data.message || '修改失败')
        return
      }
    } else {
      const res = await request.post('/customer/address', addressForm.value)
      if (res.data.code === 200) {
        ElMessage.success('添加成功')
      } else {
        ElMessage.error(res.data.message || '添加失败')
        return
      }
    }
    dialogVisible.value = false
    loadAddresses()
  } catch (e) {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    saving.value = false
  }
}

const deleteAddr = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该地址吗？', '提示', { type: 'warning' })
    const res = await request.delete(`/customer/address/${id}`)
    if (res.data.code === 200) {
      ElMessage.success('删除成功')
      loadAddresses()
    } else {
      ElMessage.error(res.data.message || '删除失败')
    }
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const setDefault = async (id) => {
  try {
    const res = await request.put(`/customer/address/${id}/default`)
    if (res.data.code === 200) {
      ElMessage.success('设置默认地址成功')
      loadAddresses()
    } else {
      ElMessage.error(res.data.message || '设置失败')
    }
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
  ElMessage.success('已退出登录')
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.user-card {
  margin-bottom: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.user-detail {
  flex: 1;
}

.user-detail h2 {
  margin: 0 0 5px 0;
  font-size: 20px;
}

.user-detail p {
  margin: 0;
  color: #999;
  font-size: 14px;
}

.shortcut-card {
  margin-bottom: 20px;
}

.shortcut-list {
  display: flex;
  justify-content: space-around;
}

.shortcut-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 10px 20px;
  border-radius: 8px;
  transition: background 0.3s;
}

.shortcut-item:hover {
  background: #f5f7fa;
}

.shortcut-item span {
  font-size: 14px;
  color: #666;
}

.address-card {
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

.empty-tip {
  padding: 20px 0;
}

.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  transition: all 0.3s;
}

.address-item:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.address-main {
  flex: 1;
}

.address-top {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.receiver-name {
  font-weight: bold;
  font-size: 15px;
}

.receiver-phone {
  color: #666;
  font-size: 14px;
}

.address-detail {
  color: #999;
  font-size: 14px;
  line-height: 1.4;
}

.address-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}
</style>
