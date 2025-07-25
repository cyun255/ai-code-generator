<template>
  <header class="header">
    <div class="logo">
      <img src="/src/assets/logo.svg" alt="Logo" class="logo-img" />
      <span class="logo-title">AI 零代码应用生成平台</span>
    </div>
    <nav class="menu">
      <a-menu mode="horizontal" :selected-keys="[activeMenu]">
        <a-menu-item v-for="item in menus" :key="item.path" @click="goRoute(item.path)">
          {{ item.title }}
        </a-menu-item>
      </a-menu>
    </nav>
    <div class="login-btn">
      <a-button type="primary">登录</a-button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const menus = [
  { title: '首页', path: '/' },
  { title: '关于', path: '/about' },
]

const activeMenu = computed(() => {
  const found = menus.find((item) => route.path === item.path)
  return found ? found.path : menus[0].path
})

function goRoute(path: string) {
  if (route.path !== path) {
    router.push(path)
  }
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
      background: transparent;
      border-bottom: none;
    }
  }
  .login-btn {
    min-width: 80px;
    display: flex;
    justify-content: flex-end;
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
