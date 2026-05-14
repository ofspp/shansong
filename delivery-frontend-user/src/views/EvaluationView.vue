<template>
  <div class="evaluation-view">
    <el-card>
      <template #header>
        <span>订单评价</span>
      </template>

      <div v-if="!submitted">
        <el-form :model="form" label-width="80px">
          <el-form-item label="评分">
            <el-rate v-model="form.rating" :colors="colors" show-text :texts="texts" />
          </el-form-item>

          <el-form-item label="评价内容">
            <el-input
              v-model="form.content"
              type="textarea"
              :rows="4"
              placeholder="请输入您的评价（选填）"
              maxlength="200"
              show-word-limit
            />
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submitEvaluation">提交评价</el-button>
          </el-form-item>
        </el-form>
      </div>

      <div v-else class="success-message">
        <el-result icon="success" title="评价成功" sub-title="感谢您的评价！">
          <template #extra>
            <el-button type="primary" @click="$router.back()">返回订单列表</el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const route = useRoute()
const orderId = route.params.id
const submitted = ref(false)

const colors = ['#F7BA2A', '#FF9900', '#FF5635']
const texts = ['很差', '较差', '一般', '满意', '非常满意']

const form = reactive({
  rating: 5,
  content: ''
})

const submitEvaluation = async () => {
  try {
    await request.post('/evaluation', null, {
      params: {
        orderId: Number(orderId),
        rating: form.rating,
        content: form.content
      }
    })
    submitted.value = true
    ElMessage.success('评价提交成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '评价失败')
  }
}
</script>

<style scoped>
.evaluation-view {
  max-width: 600px;
  margin: 20px auto;
}

.success-message {
  text-align: center;
  padding: 40px 0;
}
</style>
