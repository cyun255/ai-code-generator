<template>
  <div class="app-card">
    <a-card :bordered="false">
      <template #cover>
        <a-image :src="cover" class="app-cover" footer-position="outer" fit="cover" show-loader>
          <template #extra>
            <div class="overlay">
              <a-space class="actions">
                <a-button shape="round">
                  <template #icon>
                    <icon-eye />
                  </template>
                  <template #default> 查看对话 </template>
                </a-button>
                <a-button
                  shape="round"
                  v-show="deployKey"
                  :href="`${deployBaseUrl}/${deployKey}`"
                  target="_blank"
                >
                  <template #icon>
                    <icon-link />
                  </template>
                  <template #default> 浏览应用 </template>
                </a-button>
              </a-space>
            </div>
          </template>
        </a-image>
      </template>
      <template #actions>
        <div class="app-info">
          <a-avatar>
            <span v-show="!userAvatar">{{ userName.slice(0, 1) }} </span>
            <img v-show="userAvatar" :src="userAvatar" />
          </a-avatar>
          <div class="app-description">
            <span class="app-name">{{ appName }}</span>
            <span class="user-name">{{ userName + ' ' + createTime }}</span>
          </div>
        </div>
      </template>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { IconLink, IconEye } from '@arco-design/web-vue/es/icon'

const deployBaseUrl = import.meta.env.VITE_DEPLOY_BASE_URL

defineProps({
  appName: {
    type: String,
    default: '应用名称',
  },
  userName: {
    type: String,
    default: '用户名' as string,
  },
  userAvatar: {
    type: String,
    default: '',
  },
  cover: {
    type: String,
    default: 'https://picsum.photos/300/200',
  },
  deployKey: {
    type: String,
    default: null,
  },
  createTime: {
    type: String,
    default: new Date().toLocaleDateString(),
  },
})
</script>

<style lang="scss" scoped>
.app-card {
  position: relative;

  :deep(.arco-image-footer) {
    margin: 0;
  }

  :deep(.arco-card-body) {
    padding: 0;
  }

  :deep(.arco-card-actions) {
    margin: 5px 0;
    justify-content: flex-start;
  }

  .app-cover {
    :deep(img) {
      width: 100%;
      height: 200px;
      max-width: 300px;
      border-radius: 10px;
    }
  }

  .app-info {
    display: flex;
    .app-description {
      display: flex;
      margin-left: 12px;
      flex-direction: column;
      justify-content: space-around;

      .app-name {
        font-weight: 700;
      }
    }
  }

  &:hover {
    .overlay {
      opacity: 1;
    }
  }

  .overlay {
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    opacity: 0;
    max-width: 300px;
    position: absolute;
    background: #1119251a;
    border-radius: 10px;
    transition: opacity 0.3s ease-in-out;

    .actions {
      position: absolute;
      bottom: 24px;
      left: 50%;
      transform: translateX(-50%);
      display: flex;
      gap: 8px;

      :deep(.arco-button) {
        width: 100px;
      }
    }
  }
}
</style>
