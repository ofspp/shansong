<template>
  <div class="profile-view">
    <div class="card">
      <h3>店铺信息</h3>
      <form @submit.prevent="saveProfile">
        <div class="form-group">
          <label>店铺名称</label>
          <input v-model="form.shopName" type="text" />
        </div>
        <div class="form-group">
          <label>联系电话</label>
          <input v-model="form.phone" type="text" />
        </div>
        <div class="form-group">
          <label>营业时间</label>
          <input v-model="form.businessHours" type="text" placeholder="如: 08:00-22:00" />
        </div>
        <div class="form-group">
          <label>配送费</label>
          <input v-model.number="form.deliveryFee" type="number" step="0.01" />
        </div>
        <div class="form-group">
          <label>起送金额</label>
          <input v-model.number="form.minOrderAmount" type="number" step="0.01" />
        </div>
        <button type="submit" class="btn btn-primary">保存</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const form = ref({
  shopName: '',
  phone: '',
  businessHours: '',
  deliveryFee: 0,
  minOrderAmount: 0
})

onMounted(() => {
  loadProfile()
})

async function loadProfile() {
  try {
    const res = await request.get('/merchant/info')
    if (res.data.code === 200) {
      form.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

async function saveProfile() {
  try {
    const res = await request.put('/merchant/info', form.value)
    if (res.data.code === 200) {
      alert('保存成功')
    } else {
      alert(res.data.message)
    }
  } catch (e) {
    alert('保存失败')
  }
}
</script>

<style scoped>
.profile-view .card {
  max-width: 600px;
}

.profile-view h3 {
  margin-bottom: 20px;
}
</style>
