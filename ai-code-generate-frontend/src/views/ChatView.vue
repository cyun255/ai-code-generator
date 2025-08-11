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
              <a-button type="primary" class="deploy-btn" @click="deployModal = true">
                <template #icon><icon-upload /></template>
                <template #default>部署应用</template>
              </a-button>
              <a-button
                type="primary"
                status="danger"
                class="delete-btn"
                @click="deleteModal = true"
              >
                <template #icon><icon-delete /></template>
                <template #default>删除应用</template>
              </a-button>
            </a-space>
          </a-col>
        </a-row>
        <div ref="messageContainer" class="chat-messages">
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
          @send-msg="sendMsg"
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
    <a-modal
      title="删除应用"
      :draggable="true"
      :mask="true"
      :visible="deleteModal"
      :ok-loading="deleteLoading"
      @ok="deleteApp()"
      @cancel="deleteModal = false"
    >
      请确认是否删除该应用
    </a-modal>
    <a-modal
      title="部署应用"
      :draggable="true"
      :mask="true"
      :visible="deployModal"
      :ok-loading="deployLoading"
      @ok="deployApp"
      @cancel="deployModal = false"
    >
      <span v-if="!app?.deployKey"> 请确认是否部署该应用？ </span>
      <span v-else>
        该应用已部署，是否重新部署？<a
          :href="`${deployUrl}/${app?.deployKey}`"
          target="_blank"
          rel="noopener noreferrer"
        >
          {{ `${deployUrl}/${app?.deployKey}` }}
        </a>
      </span>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import ChatInput from '@/components/ChatInput.vue'
import MarkdownPreview from '@/components/MarkdownPreview.vue'
import request from '@/request'
import { DeleteAppById, DeployAppById, GetAppById, type AppInfo } from '@/request/app'
import type { ApiResponse } from '@/types'
import { Message } from '@arco-design/web-vue'
import { onMounted, ref, useTemplateRef, watch } from 'vue'
import { useRoute } from 'vue-router'
import { IconUpload, IconCloudDownload, IconDelete } from '@arco-design/web-vue/es/icon'
import router from '@/router'

const previewUrl = import.meta.env.VITE_PREVIEW_BASE_URL as string
const deployUrl = import.meta.env.VITE_DEPLOY_BASE_URL as string

type MessageHistory = {
  id: string | undefined
  message: string
  messageType: string
  appId: string | undefined
  userId: string | undefined
  createTime: string | undefined
  updateTime: string | undefined
}

const route = useRoute()

const width = ref(0)
const lastCreateTime = ref('')
const previewLoading = ref(false)
const app = ref<AppInfo | null>(null)
const messages = ref<MessageHistory[]>([])
const messageContainer = useTemplateRef('messageContainer')

const deleteModal = ref(false)
const deployModal = ref(false)

const deleteLoading = ref(false)
const deployLoading = ref(false)

watch(messages, () => {}, {
  deep: true,
})

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

const getAppInfo = async () => {
  const appId = route.params.id
  if (!appId) return

  const response = await GetAppById(appId as string)
  app.value = response.data || null
  if (!app.value) {
    // Handle case where app is not found
    Message.error('应用不存在')
    return false
  }
  return true
}

const getHistory = async (count: number) => {
  const history: ApiResponse<MessageHistory[]> = await request.get('/chat_history/list', {
    params: {
      appId: app.value?.id,
      count,
      lastCreateTime: lastCreateTime.value,
    },
  })
  messages.value = history.data?.reverse() || []

  lastCreateTime.value = messages.value[0]?.createTime || getNowTime()
  return messages.value.length
}

const createUserMessage = (prompt: string) => {
  const time = getNowTime()
  const msg: MessageHistory = {
    id: time,
    message: prompt,
    messageType: 'user',
    appId: app.value?.id,
    userId: app.value?.userId,
    createTime: time,
    updateTime: time,
  }
  messages.value.push(msg)
}

const createAiMessage = () => {
  const time = getNowTime()
  const msg: MessageHistory = {
    id: time,
    message: '',
    messageType: 'ai',
    appId: app.value?.id,
    userId: app.value?.userId,
    createTime: time,
    updateTime: time,
  }
  messages.value.push(msg)
}

const scrollToBottom = () => {
  messageContainer.value?.scrollTo({
    top: messageContainer.value.scrollHeight,
    behavior: 'smooth',
  })
}

const sendMsg = async (prompt: string) => {
  // 构造新的聊天气泡
  createUserMessage(prompt)
  createAiMessage()

  // 获取后端响应
  previewLoading.value = true
  const base_url = import.meta.env.VITE_API_BASE_URL
  const eventSource = new EventSource(
    `${base_url}/app/chat?appId=${app.value?.id}&message=${prompt}`,
    {
      withCredentials: true,
    },
  )
  eventSource.onmessage = (event) => {
    const data = JSON.parse(event.data)
    messages.value[messages.value.length - 1].message += data['d']
    scrollToBottom()
  }
  eventSource.onerror = (err) => {
    console.log('SSE 发生错误', err)
    eventSource.close()
  }
  eventSource.addEventListener('done', () => {
    console.log('SSE 传输结束')
    eventSource.close()
    previewLoading.value = false
  })
}

const deleteApp = async () => {
  deleteLoading.value = true
  const response = await DeleteAppById(app.value?.id as string)
  if (response.data) {
    Message.info('删除应用成功')
    router.replace({
      path: '/',
    })
  } else {
    Message.error('应用删除失败，请稍后重试')
  }
  deleteLoading.value = false
}

const deployApp = async () => {
  deployLoading.value = true
  const response = await DeployAppById(app.value?.id as string)
  if (response.code == 0) {
    Message.info('应用部署成功')
    getAppInfo()
  } else {
    Message.error('应用部署失败')
  }
  deployLoading.value = false
}

onMounted(async () => {
  lastCreateTime.value = getNowTime()
  width.value = window.innerWidth * 0.6

  // 应用不存在
  if (!(await getAppInfo())) {
    return
  }

  // 获取历史记录
  const count = await getHistory(20)
  scrollToBottom()
  if (count <= 0 && app.value) {
    // 不存在历史记录，则在页面添加初始化提示词
    sendMsg(app.value.initPrompt)
  }
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
