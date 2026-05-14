<template>
  <div class="dishes-view">
    <div class="card">
      <div class="toolbar">
        <select v-model="categoryId" @change="loadDishes">
          <option value="">全部分类</option>
          <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
        </select>
        <button class="btn btn-primary" @click="showAddModal = true">添加菜品</button>
      </div>
    </div>

    <table class="card">
      <thead>
        <tr>
          <th>图片</th>
          <th>菜品名称</th>
          <th>分类</th>
          <th>价格</th>
          <th>销量</th>
          <th>状态</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="dish in dishes" :key="dish.id">
          <td>
            <img v-if="dish.image" :src="dish.image" class="dish-thumb" />
            <span v-else class="no-image">无图片</span>
          </td>
          <td>{{ dish.name }}</td>
          <td>{{ getCategoryName(dish.categoryId) }}</td>
          <td>¥{{ dish.price }}</td>
          <td>{{ dish.sales }}</td>
          <td>
            <span class="status-tag" :class="dish.status === 1 ? 'status-success' : 'status-error'">
              {{ dish.status === 1 ? '上架' : '下架' }}
            </span>
          </td>
          <td>
            <button class="btn" @click="editDish(dish)">编辑</button>
            <button class="btn" :class="dish.status === 1 ? 'btn-danger' : 'btn-success'" 
                    @click="toggleStatus(dish)">
              {{ dish.status === 1 ? '下架' : '上架' }}
            </button>
            <button class="btn btn-danger" @click="deleteDish(dish.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showAddModal" class="modal">
      <div class="modal-content">
        <h3>{{ editingDish ? '编辑菜品' : '添加菜品' }}</h3>
        <div class="form-group">
          <label>菜品名称</label>
          <input v-model="dishForm.name" type="text" />
        </div>
        <div class="form-group">
          <label>分类</label>
          <select v-model="dishForm.categoryId">
            <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
          </select>
        </div>
        <div class="form-group">
          <label>价格</label>
          <input v-model.number="dishForm.price" type="number" step="0.01" />
        </div>
        <div class="form-group">
          <label>描述</label>
          <textarea v-model="dishForm.description"></textarea>
        </div>
        <div class="form-group">
          <label>菜品图片</label>
          <div class="image-upload-area">
            <img v-if="dishForm.image" :src="dishForm.image" class="preview-image" />
            <div class="upload-btn-wrap">
              <input type="file" accept="image/*" @change="handleImageUpload" ref="fileInput" class="file-input" />
              <button type="button" class="btn btn-primary" @click="$refs.fileInput.click()">
                {{ uploading ? '上传中...' : (dishForm.image ? '更换图片' : '选择图片') }}
              </button>
              <button v-if="dishForm.image" type="button" class="btn btn-danger" @click="dishForm.image = ''">删除图片</button>
            </div>
          </div>
        </div>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="saveDish">保存</button>
          <button class="btn" @click="closeModal">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const dishes = ref([])
const categories = ref([])
const categoryId = ref('')
const showAddModal = ref(false)
const editingDish = ref(null)

const dishForm = ref({
  name: '',
  categoryId: null,
  price: 0,
  description: '',
  image: ''
})
const uploading = ref(false)
const fileInput = ref(null)

onMounted(() => {
  loadCategories()
  loadDishes()
})

async function loadCategories() {
  try {
    const res = await request.get('/merchant/categories')
    if (res.data.code === 200) {
      categories.value = res.data.data
    }
  } catch (e) {
    console.error(e)
  }
}

async function loadDishes() {
  try {
    const res = await request.get('/merchant/dishes', {
      params: { page: 1, size: 100, categoryId: categoryId.value }
    })
    if (res.data.code === 200) {
      dishes.value = res.data.data.records
    }
  } catch (e) {
    console.error(e)
  }
}

function getCategoryName(catId) {
  const cat = categories.value.find(c => c.id === catId)
  return cat ? cat.name : ''
}

function editDish(dish) {
  editingDish.value = dish
  dishForm.value = { name: dish.name, categoryId: dish.categoryId, price: dish.price, description: dish.description, image: dish.image || '' }
  showAddModal.value = true
}

async function saveDish() {
  try {
    let res
    const data = {
      name: dishForm.value.name,
      categoryId: dishForm.value.categoryId,
      price: dishForm.value.price,
      description: dishForm.value.description,
      image: dishForm.value.image
    }
    if (editingDish.value) {
      res = await request.put('/merchant/dish', { ...data, id: editingDish.value.id })
    } else {
      res = await request.post('/merchant/dish', data)
    }
    if (res.data.code === 200) {
      closeModal()
      loadDishes()
    } else {
      alert(res.data.message)
    }
  } catch (e) {
    alert('操作失败')
  }
}

async function toggleStatus(dish) {
  try {
    const newStatus = dish.status === 1 ? 0 : 1
    const res = await request.put(`/merchant/dish/${dish.id}/status`, null, {
      params: { status: newStatus }
    })
    if (res.data.code === 200) {
      loadDishes()
    }
  } catch (e) {
    alert('操作失败')
  }
}

async function deleteDish(dishId) {
  if (!confirm('确定要删除此菜品吗？')) return
  try {
    const res = await request.delete(`/merchant/dish/${dishId}`)
    if (res.data.code === 200) {
      loadDishes()
    }
  } catch (e) {
    alert('操作失败')
  }
}

function closeModal() {
  showAddModal.value = false
  editingDish.value = null
  dishForm.value = { name: '', categoryId: null, price: 0, description: '', image: '' }
}

async function handleImageUpload(event) {
  const file = event.target.files[0]
  if (!file) return
  
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    alert('只支持 jpg/png/gif/webp 格式的图片')
    return
  }
  if (file.size > 10 * 1024 * 1024) {
    alert('图片大小不能超过10MB')
    return
  }
  
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await request.post('/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
    if (res.data.code === 200) {
      dishForm.value.image = res.data.data.url
    } else {
      alert(res.data.message || '上传失败')
    }
  } catch (e) {
    alert('图片上传失败')
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toolbar select {
  width: 200px;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  width: 500px;
}

.modal-content h3 {
  margin-bottom: 20px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.dish-thumb {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  object-fit: cover;
}

.no-image {
  color: #999;
  font-size: 12px;
}

.image-upload-area {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.preview-image {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  object-fit: cover;
  border: 1px solid #eee;
}

.upload-btn-wrap {
  display: flex;
  gap: 10px;
  align-items: center;
}

.file-input {
  display: none;
}
</style>
