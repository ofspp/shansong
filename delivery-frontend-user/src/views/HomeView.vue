<template>
  <div class="home-container">
    <!-- 推荐商家 -->
    <div class="recommend-section" v-if="recommendedMerchants.length > 0">
      <h2 class="section-title">👍 热门推荐</h2>
      <div class="recommend-list">
        <el-card
          v-for="merchant in recommendedMerchants"
          :key="'rec-' + merchant.id"
          class="recommend-card"
          @click="navigateToMerchant(merchant.id)"
        >
          <div class="recommend-header">
            <div class="recommend-info">
              <h4>{{ merchant.shopName }}</h4>
              <span class="likes-badge">👍 {{ merchant.likesCount || 0 }}</span>
            </div>
            <div class="recommend-meta">
              <span>配送费: ¥{{ merchant.deliveryFee }}</span>
              <span>起送: ¥{{ merchant.minOrderAmount }}</span>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索商家"
        prefix-icon="el-icon-search"
        @keyup.enter="searchMerchants"
      >
        <template #append>
          <el-button @click="searchMerchants">搜索</el-button>
        </template>
      </el-input>
    </div>

    <!-- 商家列表 -->
    <h2 class="section-title" v-if="merchants.length > 0">全部商家</h2>
    <div class="merchant-list">
      <el-card
        v-for="merchant in merchants"
        :key="merchant.id"
        class="merchant-card"
      >
        <div class="merchant-header" @click="navigateToMerchant(merchant.id)">
          <img :src="merchant.shopLogo || defaultLogo" :alt="merchant.shopName" class="merchant-logo" />
          <div class="merchant-info">
            <h3>{{ merchant.shopName }}</h3>
            <div class="merchant-details">
              <span class="delivery-fee">配送费: ¥{{ merchant.deliveryFee }}</span>
              <span class="min-order">起送: ¥{{ merchant.minOrderAmount }}</span>
              <span class="business-hours">{{ merchant.businessHours }}</span>
            </div>
          </div>
        </div>
        <div class="merchant-bottom">
          <div class="merchant-address" @click="navigateToMerchant(merchant.id)">
            {{ merchant.province }}{{ merchant.city }}{{ merchant.district }}{{ merchant.address }}
          </div>
          <el-button
            :type="likedIds.includes(merchant.id) ? 'danger' : 'default'"
            size="small"
            @click.stop="toggleLike(merchant)"
            :loading="likingId === merchant.id"
          >
            👍 {{ merchant.likesCount || 0 }}
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

const keyword = ref('')
const page = ref(1)
const size = ref(10)
const merchants = ref([])
const total = ref(0)
const recommendedMerchants = ref([])
const likedIds = ref([])
const likingId = ref(null)
const defaultLogo = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI4MCIgaGVpZ2h0PSI4MCIgdmlld0JveD0iMCAwIDgwIDgwIj48cmVjdCB3aWR0aD0iODAiIGhlaWdodD0iODAiIGZpbGw9IiNmMGYwZjAiLz48dGV4dCB4PSI1MCUiIHk9IjUwJSIgZG9taW5hbnQtYmFzZWxpbmU9Im1pZGRsZSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZm9udC1zaXplPSIzMCIgZmlsbD0iI2NjYyI+8J+NvTwvdGV4dD48L3N2Zz4='

const loadMerchants = async () => {
  try {
    const response = await request.get('/customer/merchants', {
      params: {
        page: page.value,
        size: size.value,
        keyword: keyword.value
      }
    })
    
    if (response.data.code === 200) {
      merchants.value = response.data.data.records
      total.value = response.data.data.total
    } else {
      ElMessage.error(response.data.message || '获取商家列表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('获取商家列表失败:', error)
  }
}

const searchMerchants = () => {
  page.value = 1
  loadMerchants()
}

const handleSizeChange = (newSize) => {
  size.value = newSize
  loadMerchants()
}

const handleCurrentChange = (newPage) => {
  page.value = newPage
  loadMerchants()
}

const navigateToMerchant = (merchantId) => {
  router.push(`/merchant/${merchantId}`)
}

const loadRecommended = async () => {
  try {
    const res = await request.get('/customer/recommended-merchants', { params: { limit: 6 } })
    if (res.data.code === 200) {
      recommendedMerchants.value = res.data.data
    }
  } catch (e) {
    console.error('获取推荐商家失败:', e)
  }
}

const loadLikedIds = async () => {
  try {
    const res = await request.get('/customer/liked-merchants')
    if (res.data.code === 200) {
      likedIds.value = res.data.data
    }
  } catch (e) {
    // 未登录时忽略
  }
}

const toggleLike = async (merchant) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录后再点赞')
    router.push('/login')
    return
  }
  likingId.value = merchant.id
  try {
    const res = await request.post(`/customer/merchant/${merchant.id}/like`)
    if (res.data.code === 200) {
      const { liked, likesCount } = res.data.data
      merchant.likesCount = likesCount
      if (liked) {
        if (!likedIds.value.includes(merchant.id)) likedIds.value.push(merchant.id)
        ElMessage.success('点赞成功')
      } else {
        likedIds.value = likedIds.value.filter(id => id !== merchant.id)
        ElMessage.info('已取消点赞')
      }
      loadRecommended()
    }
  } catch (e) {
    if (e.response && (e.response.status === 401 || e.response.status === 403)) {
      ElMessage.warning('请先登录后再点赞')
      userStore.logout()
      router.push('/login')
    } else {
      ElMessage.error('操作失败，请稍后重试')
    }
  } finally {
    likingId.value = null
  }
}

onMounted(() => {
  loadMerchants()
  loadRecommended()
  if (userStore.token) {
    loadLikedIds()
  }
})
</script>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  margin: 20px 0 15px;
  color: #333;
}

.recommend-section {
  margin-bottom: 10px;
}

.recommend-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 10px;
}

.recommend-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.recommend-card:hover {
  transform: translateY(-3px);
}

.recommend-card :deep(.el-card__body) {
  padding: 14px;
}

.recommend-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.recommend-info h4 {
  margin: 0;
  font-size: 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.likes-badge {
  font-size: 13px;
  color: #ff4d4f;
  white-space: nowrap;
  margin-left: 8px;
}

.recommend-meta {
  display: flex;
  gap: 10px;
  font-size: 12px;
  color: #999;
}

.search-bar {
  margin-bottom: 20px;
}

.merchant-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.merchant-card {
  cursor: pointer;
  transition: transform 0.3s ease;
}

.merchant-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
}

.merchant-header {
  display: flex;
  margin-bottom: 10px;
}

.merchant-logo {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  margin-right: 15px;
  object-fit: cover;
  background: #f0f0f0;
}

.merchant-info {
  flex: 1;
}

.merchant-info h3 {
  margin: 0 0 10px 0;
  font-size: 18px;
  font-weight: bold;
}

.merchant-details {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  font-size: 14px;
  color: #666;
}

.merchant-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.merchant-address {
  font-size: 14px;
  color: #999;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
