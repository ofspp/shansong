<template>
  <div class="profile-view">
    <div class="card">
      <h3>个人信息</h3>
      <div class="info-row">
        <span class="label">用户名:</span>
        <span>{{ riderInfo.username }}</span>
      </div>
      <div class="info-row">
        <span class="label">真实姓名:</span>
        <span>{{ riderInfo.realName }}</span>
      </div>
      <div class="info-row">
        <span class="label">手机号:</span>
        <span>{{ riderInfo.phone }}</span>
      </div>
      <div class="info-row">
        <span class="label">车辆类型:</span>
        <span>{{ riderInfo.vehicleType }}</span>
      </div>
      <div class="info-row">
        <span class="label">车牌号:</span>
        <span>{{ riderInfo.vehicleNo }}</span>
      </div>
      <div class="info-row">
        <span class="label">工作状态:</span>
        <span class="status-tag" :class="riderInfo.workingStatus === 1 ? 'status-success' : 'status-warning'">
          {{ riderInfo.workingStatus === 1 ? '接单中' : '休息中' }}
        </span>
      </div>
      <div class="actions">
        <button class="btn" :class="riderInfo.workingStatus === 1 ? 'btn-danger' : 'btn-success'" @click="toggleWorkingStatus">
          {{ riderInfo.workingStatus === 1 ? '停止接单' : '开始接单' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const riderInfo = ref({})

onMounted(() => {
  loadRiderInfo()
})

async function loadRiderInfo() {
  try {
    const res = await request.get('/rider/info')
    if (res.data.code === 200) {
      riderInfo.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

async function toggleWorkingStatus() {
  try {
    const newStatus = riderInfo.value.workingStatus === 1 ? 0 : 1
    const res = await request.put('/rider/working-status', null, {
      params: { status: newStatus }
    })
    if (res.data.code === 200) {
      riderInfo.value.workingStatus = newStatus
    } else {
      alert(res.data.message || '操作失败')
    }
  } catch (e) {
    if (e.response) {
      const status = e.response.status
      const data = e.response.data
      if (status === 401) {
        alert('登录已过期，请重新登录')
      } else if (status === 403) {
        alert('无权限执行此操作，请重新登录')
      } else {
        alert(data?.message || `请求失败 (${status})`)
      }
    } else if (e.request) {
      alert('网络连接失败，请检查后端服务是否启动')
    } else {
      alert('操作失败: ' + (e.message || '未知错误'))
    }
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

.info-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-row .label {
  width: 100px;
  color: #666;
}

.actions {
  margin-top: 20px;
}
</style>
