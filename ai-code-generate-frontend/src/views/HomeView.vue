<template>
  <div class="home-view">
    <div class="chat">
      <div class="title">
        <img src="/src/assets/logo.svg" alt="Logo" class="logo-img" />
        <span class="text">AI 应用生成平台</span>
      </div>
      <chat-input placeholder="创建一个博客网站....... " @send-msg="createApp" class="chat-input" />
    </div>
    <div class="exhibition">
      <div class="self" v-show="userStore.userInfo.id">
        <h2>我的应用</h2>
        <a-spin :loading="userAppsLoading" tip="加载中...">
          <a-grid :cols="gridCols" :col-gap="4" :row-gap="16" v-if="userApps.length > 0">
            <a-grid-item v-for="app in userApps" :key="app.id">
              <app-card
                :app-id="app.id"
                :app-name="app.name"
                :cover="app.cover || undefined"
                :user-name="app.userVO.name"
                :user-avatar="app.userVO.avatar || undefined"
                :deploy-key="app.deployKey || undefined"
                :create-time="new Date(app.createTime).toLocaleDateString()"
              />
            </a-grid-item>
          </a-grid>
          <a-empty v-else />
          <a-grid :cols="gridCols" :col-gap="16" :row-gap="16">
            <a-grid-item>
              <div style="width: 300px" />
            </a-grid-item>
          </a-grid>
        </a-spin>
      </div>
      <div class="feature">
        <h2>精选推荐</h2>
        <a-grid :cols="gridCols" :col-gap="16" :row-gap="16">
          <a-grid-item>
            <div style="width: 300px" />
          </a-grid-item>
        </a-grid>
        <a-empty />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import AppCard from '@/components/AppCard.vue'
import { onMounted, ref } from 'vue'
import { useUserStore } from '@/stores/user'
import { CreateApp, PageUserApps, type AppInfo } from '@/request/app'
import { Message } from '@arco-design/web-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const userStore = useUserStore()
const userApps = ref<AppInfo[]>([])

const userAppsLoading = ref(false)

const gridCols = {
  xs: 1,
  sm: 1,
  md: 2,
  lg: 3,
  xl: 4,
}

const createApp = async (initPrompt: string) => {
  const response = await CreateApp(initPrompt)
  if (response.code === 0) {
    Message.info('应用创建成功')
    router.push(`/chat/${response.data}`)
  } else {
    Message.error(response.message)
  }
}

const fetchUserApps = async () => {
  if (!userStore.userInfo.id) {
    return
  }

  userAppsLoading.value = true
  const pages = await PageUserApps(1, 10)
  userApps.value = pages.data?.records as AppInfo[]
  userAppsLoading.value = false
}

onMounted(() => {
  fetchUserApps()
})
</script>

<style lang="scss" scoped>
.home-view {
  background-size: cover;
  background-repeat: no-repeat;
  background-position: top;
  background-image: url('https://s3.meituan.net/static-prod01/ws-assets/com.sankuai.nocode.external.frontend/hf0cdwsr60JTIAhFLbMWE/assets/background-Ka9zUI9d.png');
  .chat {
    height: 60vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    .title {
      display: flex;
      align-items: center;
      padding-bottom: 2.5rem;

      .logo-img {
        width: 64px;
        height: 64px;
        margin-right: 8px;
      }

      .text {
        font-size: 36px;
        font-weight: bold;
      }
    }

    .chat-input {
      width: 100%;
      max-width: 48rem;
    }
  }

  .exhibition {
    display: flex;
    align-items: center;
    flex-direction: column;
    justify-content: center;

    .self,
    .feature {
      margin: 20px;
      padding: 15px 20px;
      max-width: 1400px;
      background-color: white;
      border-radius: 25px;
      min-height: 200px;
    }
  }
}
</style>
