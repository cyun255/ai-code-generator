<template>
  <div class="chat-view">
    <div class="chat-area">
      <div class="chat-content">
        <a-row class="header">
          <a-col flex="200px" class="title">{{ app?.name }}</a-col>
          <a-col flex="auto"></a-col>
          <a-col flex="300px">
            <a-space>
              <a-button type="secondary" class="download-btn">
                <template #icon><icon-cloud-download /></template>
                <template #default>下载代码</template>
              </a-button>
              <a-button type="primary" class="deploy-btn">
                <template #icon><icon-upload /></template>
                <template #default>部署应用</template>
              </a-button>
              <a-button type="primary" status="danger" class="delete-btn">
                <template #icon><icon-delete /></template>
                <template #default>删除应用</template>
              </a-button>
            </a-space>
          </a-col>
        </a-row>
        <div class="chat-messages">
          <div v-for="msg in messages" :key="msg.id">
            <div v-if="msg.messageType === 'user'" class="user-msg">
              <p>{{ msg.message }}</p>
            </div>
            <MarkdownPreview v-else class="ai-msg" :source="msg.message"></MarkdownPreview>
          </div>
        </div>
        <ChatInput
          class="chat-input"
          placeholder="描述越详细，页面越具体，还可以一步一步完善生成效果"
          style="--background-color-inner: #f5f6f6; --border-radius-size: 0px"
          @send-msg="(prompt: string) => console.log(prompt)"
        />
      </div>
    </div>
    <a-resize-box
      class="resize-box"
      :directions="['left']"
      v-model:width="width"
      @moving-start="() => (previewLoading = true)"
      @moving-end="() => (previewLoading = false)"
    >
      <a-spin class="preview-loading" :loading="previewLoading" tip="加载中...">
        <iframe
          :src="!previewLoading ? `${previewUrl}/${app?.codeGenType}_${app?.id as string}` : ''"
          width="100%"
          height="100%"
          frameborder="0"
        ></iframe>
      </a-spin>
      <template #resize-trigger>
        <div class="resize-trigger"></div>
      </template>
    </a-resize-box>
  </div>
</template>

<script lang="ts" setup>
import ChatInput from '@/components/ChatInput.vue'
import MarkdownPreview from '@/components/MarkdownPreview.vue'
import request from '@/request'
import { GetAppById, type AppInfo } from '@/request/app'
import type { ApiResponse } from '@/types'
import { Message } from '@arco-design/web-vue'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { IconUpload, IconCloudDownload, IconDelete } from '@arco-design/web-vue/es/icon'

const previewUrl = import.meta.env.VITE_PREVIEW_BASE_URL as string

type Message = {
  id: string
  message: string
  messageType: string
  appId: string
  userId: string
  createTime: string
  updateTime: string
}

const route = useRoute()

const width = ref(0)
const previewLoading = ref(false)
const app = ref<AppInfo | null>(null)
const messages = ref<Message[]>([])

const getNowTime = () => {
  const date = new Date()
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()
  return `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

window.onresize = () => {
  width.value = window.innerWidth * 0.6
}

onMounted(async () => {
  width.value = window.innerWidth * 0.6
  const lastCreateTime = getNowTime()

  const appId = route.params.id
  if (!appId) return

  const response = await GetAppById(appId as string)
  app.value = response.data || null
  if (!app.value) {
    // Handle case where app is not found
    Message.error('应用不存在')
    return
  }

  const history: ApiResponse<Message[]> = await request.get('/chat_history/list', {
    params: {
      appId: 1,
      count: 20,
      lastCreateTime: lastCreateTime,
    },
  })
  messages.value = history.data?.reverse() || []
})
</script>

<style lang="scss" scoped>
.chat-view {
  font-size: 16px;
  display: flex;
  padding: 20px 10px;
  justify-content: space-between;
  height: calc(100vh - 161px);
  .chat-area {
    flex: 1;
    background-color: white;
    overflow-y: auto;
    .chat-content {
      height: 100%;
      display: flex;
      overflow: hidden;
      flex-direction: column;
      background-color: #f3f3f4;
      .header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        border: 1px solid #e8e8e8;
        background-color: white;
        padding: 5px 25px;
        .title {
          font-weight: 600;
          margin-right: auto;
        }
        .download-btn,
        .deploy-btn,
        .delete-btn {
          border-radius: 5px;
        }
      }
      .chat-messages {
        flex: 1;
        overflow-y: auto;
        .user-msg {
          color: white;
          display: flex;
          border-radius: 5px;
          flex-direction: row-reverse;
          & > p {
            padding: 10px 20px;
            margin-right: 10px;
            border-radius: 10px;
            background-color: rgb(var(--primary-5));
          }
        }
      }
      .chat-input {
        margin-top: 5px;
        border: 1px solid #e8e8e8;
      }
    }
  }
  .resize-box {
    border: 1px solid #ccc;
    .preview-loading {
      width: 100%;
      height: 100%;
      .preview-container {
        width: 100%;
        height: 100%;
      }
    }
    .resize-trigger {
      content: '';
      width: 3px;
      height: 100%;
      cursor: ew-resize;
      background-color: darken(#3f3c3c, 5%);
      opacity: 0;
      transition: opacity 0.2s ease;
      &:hover {
        opacity: 1;
      }
    }
  }
}
</style>
