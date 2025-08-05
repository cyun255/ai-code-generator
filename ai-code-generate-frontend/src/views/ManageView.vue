<template>
  <div class="manage-view">
    <!-- 搜索表单 -->
    <a-card class="search-card">
      <a-form :model="searchForm" layout="inline" @submit="handleSearch" class="search-form">
        <a-form-item field="id" label="用户ID">
          <a-input-number
            v-model="searchForm.id"
            placeholder="用户ID"
            :min="1"
            style="width: 120px"
          />
        </a-form-item>
        <a-form-item field="username" label="用户名">
          <a-input v-model="searchForm.username" placeholder="请输入用户名" style="width: 150px" />
        </a-form-item>
        <a-form-item field="name" label="昵称">
          <a-input v-model="searchForm.name" placeholder="请输入昵称" style="width: 150px" />
        </a-form-item>
        <a-form-item field="profile" label="简介">
          <a-input v-model="searchForm.profile" placeholder="请输入简介" style="width: 150px" />
        </a-form-item>
        <a-form-item field="role" label="角色">
          <a-select
            v-model="searchForm.role"
            placeholder="请选择角色"
            style="width: 120px"
            allow-clear
          >
            <a-option :value="0">管理员</a-option>
            <a-option :value="1">普通用户</a-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit" :loading="loading">
              <template #icon>
                <icon-search />
              </template>
              搜索
            </a-button>
            <a-button @click="handleReset">
              <template #icon>
                <icon-refresh />
              </template>
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 用户表格 -->
    <a-card class="table-card">
      <a-table
        :data="tableData"
        :loading="loading"
        :pagination="pagination"
        @page-change="handlePageChange"
        @page-size-change="handlePageSizeChange"
      >
        <template #columns>
          <a-table-column title="用户ID" data-index="id" align="center" />
          <a-table-column title="用户信息">
            <template #cell="{ record }">
              <div class="user-info">
                <a-avatar class="user-avatar">
                  <span v-show="!record.avatar">{{ record.name?.slice(0, 1) }}</span>
                  <img v-show="record.avatar" :src="record.avatar" />
                </a-avatar>
                <div class="user-details">
                  <div class="user-name">{{ record.name }}</div>
                  <div class="user-username">@{{ record.username }}</div>
                </div>
              </div>
            </template>
          </a-table-column>
          <a-table-column title="简介" data-index="profile" />
          <a-table-column title="角色" align="center">
            <template #cell="{ record }">
              <a-tag :color="record.role === 0 ? 'red' : 'blue'">
                {{ record.role === 0 ? '管理员' : '普通用户' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="创建时间" data-index="createTime" align="center" />
          <a-table-column title="更新时间" data-index="updateTime" align="center" />
          <a-table-column title="是否删除" data-index="isDelete" align="center">
            <template #cell="{ record }">
              <a-tag :color="record.isDelete === 0 ? 'green' : 'red'">
                {{ record.isDelete === 0 ? '正常' : '已删除' }}
              </a-tag>
            </template>
          </a-table-column>
          <a-table-column title="操作" align="center">
            <template #cell="{ record }">
              <a-button type="text" size="small" @click="handleEdit(record)">
                <template #icon>
                  <icon-edit />
                </template>
                编辑
              </a-button>
            </template>
          </a-table-column>
        </template>
      </a-table>
    </a-card>

    <!-- 编辑用户对话框 -->
    <a-modal
      v-model:visible="editModalVisible"
      title="编辑用户信息"
      @ok="handleEditSubmit"
      @cancel="handleEditCancel"
      :confirm-loading="editLoading"
    >
      <a-form :model="editForm" layout="vertical">
        <a-form-item label="用户ID" field="id">
          <a-input v-model="editForm.id" disabled />
        </a-form-item>
        <a-form-item label="用户名" field="username">
          <a-input v-model="editForm.username" />
        </a-form-item>
        <a-form-item label="昵称" field="name">
          <a-input v-model="editForm.name" placeholder="请输入昵称" />
        </a-form-item>
        <a-form-item label="简介" field="profile">
          <a-textarea v-model="editForm.profile" placeholder="请输入简介" :rows="3" />
        </a-form-item>
        <a-form-item label="角色" field="role">
          <a-select v-model="editForm.role" placeholder="请选择角色">
            <a-option :value="0">管理员</a-option>
            <a-option :value="1">普通用户</a-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Message } from '@arco-design/web-vue'
import { IconSearch, IconRefresh, IconEdit } from '@arco-design/web-vue/es/icon'
import request from '@/request'
import type { ApiResponse } from '@/types'

// 类型定义
interface UserVO {
  id: number
  username: string
  name: string
  avatar?: string
  profile?: string
  role: number
  createTime: string
  updateTime: string
  isDelete: number
}

interface PageUserVO {
  records: UserVO[]
  pageNumber: number
  pageSize: number
  totalPage: number
  totalRow: number
}

interface SearchForm {
  id?: number
  username?: string
  name?: string
  profile?: string
  role?: number
}

interface EditForm {
  id: number
  username: string
  name: string
  profile?: string
  role: number
}

// 响应式数据
const loading = ref(false)
const tableData = ref<UserVO[]>([])
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: true,
  showJumper: true,
  showPageSize: true,
})

const searchForm = reactive<SearchForm>({})
const editModalVisible = ref(false)
const editLoading = ref(false)
const editForm = reactive<EditForm>({
  id: 0,
  username: '',
  name: '',
  profile: '',
  role: 1,
})

// 获取用户列表
const fetchUserList = async () => {
  loading.value = true
  try {
    const params = {
      current: pagination.current,
      pageSize: pagination.pageSize,
      ...searchForm,
    }

    const response: ApiResponse<PageUserVO> = await request.get('/user/page', { params })

    if (response.code === 0 && response.data) {
      tableData.value = response.data.records
      pagination.total = response.data.totalRow
    } else {
      Message.error(response.message || '获取用户列表失败')
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    Message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = () => {
  pagination.current = 1
  fetchUserList()
}

// 重置搜索
const handleReset = () => {
  Object.keys(searchForm).forEach((key) => {
    ;(searchForm as Record<string, unknown>)[key] = undefined
  })
  pagination.current = 1
  fetchUserList()
}

// 分页处理
const handlePageChange = (page: number) => {
  pagination.current = page
  fetchUserList()
}

const handlePageSizeChange = (pageSize: number) => {
  pagination.pageSize = pageSize
  pagination.current = 1
  fetchUserList()
}

// 编辑用户
const handleEdit = (record: UserVO) => {
  editForm.id = record.id
  editForm.username = record.username
  editForm.name = record.name
  editForm.profile = record.profile || ''
  editForm.role = record.role
  editModalVisible.value = true
}

// 提交编辑
const handleEditSubmit = async () => {
  editLoading.value = true
  try {
    const response: ApiResponse<UserVO> = await request.post('/user/update', editForm)

    if (response.code === 0) {
      Message.success('更新用户信息成功')
      editModalVisible.value = false
      fetchUserList()
    } else {
      Message.error(response.message || '更新用户信息失败')
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    Message.error('更新用户信息失败')
  } finally {
    editLoading.value = false
  }
}

// 取消编辑
const handleEditCancel = () => {
  editModalVisible.value = false
}

// 初始化
onMounted(() => {
  fetchUserList()
})
</script>

<style lang="scss" scoped>
.manage-view {
  padding: 0 24px;
  margin: 0 auto;

  .search-card {
    margin-bottom: 16px;
    .search-form {
      justify-content: flex-end;
    }
  }

  .table-card {
    .user-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .user-avatar {
        background-color: #3370ff;
        flex-shrink: 0;
      }

      .user-details {
        .user-name {
          font-weight: 500;
          color: #1d2129;
          margin-bottom: 2px;
        }

        .user-username {
          font-size: 12px;
          color: #86909c;
        }
      }
    }
  }
}

// 响应式布局
@media (max-width: 768px) {
  .manage-view {
    padding: 16px;

    .page-header {
      h1 {
        font-size: 20px;
      }
    }
  }
}

@media (max-width: 480px) {
  .manage-view {
    padding: 12px;

    .page-header {
      h1 {
        font-size: 18px;
      }
    }
  }
}
</style>
