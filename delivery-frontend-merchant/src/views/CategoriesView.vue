<template>
  <div class="categories-view">
    <div class="card">
      <div class="toolbar">
        <h3>分类管理</h3>
        <button class="btn btn-primary" @click="showAddModal = true">添加分类</button>
      </div>
    </div>

    <table class="card">
      <thead>
        <tr>
          <th>分类名称</th>
          <th>排序</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="cat in categories" :key="cat.id">
          <td>{{ cat.name }}</td>
          <td>{{ cat.sort }}</td>
          <td>
            <button class="btn" @click="editCategory(cat)">编辑</button>
            <button class="btn btn-danger" @click="deleteCategory(cat.id)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <div v-if="showAddModal" class="modal">
      <div class="modal-content">
        <h3>{{ editingCategory ? '编辑分类' : '添加分类' }}</h3>
        <div class="form-group">
          <label>分类名称</label>
          <input v-model="categoryForm.name" type="text" />
        </div>
        <div class="form-group">
          <label>排序</label>
          <input v-model.number="categoryForm.sort" type="number" />
        </div>
        <div class="modal-actions">
          <button class="btn btn-primary" @click="saveCategory">保存</button>
          <button class="btn" @click="closeModal">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../utils/request'

const categories = ref([])
const showAddModal = ref(false)
const editingCategory = ref(null)

const categoryForm = ref({
  name: '',
  sort: 0
})

onMounted(() => {
  loadCategories()
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

function editCategory(cat) {
  editingCategory.value = cat
  categoryForm.value = { ...cat }
  showAddModal.value = true
}

async function saveCategory() {
  try {
    let res
    if (editingCategory.value) {
      res = await request.put('/merchant/category', { ...categoryForm.value, id: editingCategory.value.id })
    } else {
      res = await request.post('/merchant/category', categoryForm.value)
    }
    if (res.data.code === 200) {
      closeModal()
      loadCategories()
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

async function deleteCategory(catId) {
  if (!confirm('确定要删除此分类吗？')) return
  try {
    const res = await request.delete(`/merchant/category/${catId}`)
    if (res.data.code === 200) {
      loadCategories()
    } else {
      alert(res.data.message)
    }
  } catch (e) {
    alert('操作失败')
  }
}

function closeModal() {
  showAddModal.value = false
  editingCategory.value = null
  categoryForm.value = { name: '', sort: 0 }
}
</script>

<style scoped>
.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
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
  width: 400px;
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
</style>
