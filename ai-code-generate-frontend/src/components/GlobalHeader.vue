<template>
  <header class="header">
    <div class="logo">
      <img src="/src/assets/logo.svg" alt="Logo" class="logo-img" />
      <span class="logo-title">AI 应用生成平台</span>
    </div>
    <nav class="menu">
      <a-menu mode="horizontal" :selected-keys="[activeMenu]">
        <a-menu-item v-for="item in menus" :key="item.path" @click="goRoute(item.path)">
          {{ item.title }}
        </a-menu-item>
      </a-menu>
    </nav>
    <div class="login-btn">
      <a-button v-if="!userStore.userInfo.id" type="primary" @click="router.push('/login')"
        >登录
      </a-button>
      <a-dropdown v-else trigger="hover">
        <a-space>
          <a-avatar class="user-avatar">
            <span v-if="!userStore.userInfo.avatar"
              >{{ userStore.userInfo.name?.slice(0, 1) }}
            </span>
            <img v-else :src="userStore.userInfo.avatar" />
          </a-avatar>
          {{ userStore.userInfo.name || '未登录' }}
        </a-space>
        <template #content>
          <a-doption @click="logout">
            <template #icon> <icon-poweroff /> </template>
            <template #default> 退出登录 </template>
          </a-doption>
        </template>
      </a-dropdown>
    </div>
  </header>
</template>

<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { IconPoweroff } from '@arco-design/web-vue/es/icon'
import { Message } from '@arco-design/web-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

onMounted(() => {
  userStore.getInfo()
})

const menus = computed(() => {
  const baseMenus = [
    { title: '首页', path: '/' },
    { title: '用户管理', path: '/admin/manage/user' },
    { title: '关于', path: '/about' },
  ]

  // 只有管理员才显示管理页面
  return baseMenus.filter((item) => {
    if (item.path.includes('/admin') && userStore.userInfo.role !== 0) {
      return false
    }
    return true
  })
})

const activeMenu = computed(() => {
  const found = menus.value.find(
    (item: { title: string; path: string }) => route.path === item.path,
  )
  return found?.path || ''
})

function goRoute(path: string) {
  if (route.path !== path) {
    router.push(path)
  }
}

const logout = () => {
  userStore.logout()
  Message.success('已退出登录')
}
</script>

<style lang="scss" scoped>
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  padding: 0 32px;
  background: #fff;
  box-shadow: 0 2px 8px #f0f1f2;

  .logo {
    display: flex;
    align-items: center;
    .logo-img {
      width: 32px;
      height: 32px;
      margin-right: 8px;
    }
    .logo-title {
      font-size: 20px;
      font-weight: bold;
      color: #1d2129;
    }
  }
  .menu {
    flex: 1;
    display: flex;
    justify-content: center;
    margin: 0 32px;
    :deep(.arco-menu) {
      border-bottom: none;
      background-color: transparent;
    }
  }
  .login-btn {
    min-width: 80px;
    display: flex;
    justify-content: flex-end;
    .user-avatar {
      background-color: #3370ff;
    }
  }
}

@media (max-width: 600px) {
  .header {
    padding: 0 8px;
    .logo {
      display: none;
    }
    .menu {
      margin: 0;
    }
  }
}
</style>
