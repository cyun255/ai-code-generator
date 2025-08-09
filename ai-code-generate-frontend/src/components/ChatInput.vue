<template>
  <div class="chat-input">
    <a-textarea
      placeholder="创建一个博客网站......."
      :auto-size="{ minRows: 3, maxRows: 5 }"
      rows="4"
      @input="prompt = $event"
    />
    <div class="actions">
      <a-button class="send-btn" shape="circle" @click="sendMessage" :disabled="!prompt.trim()">
        <icon-arrow-up />
      </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { IconArrowUp } from '@arco-design/web-vue/es/icon'

const prompt = ref('')
const sendMessage = () => {
  if (!prompt.value.trim()) {
    return
  }
  console.log('发送消息:', prompt.value)
}
</script>

<style lang="scss" scoped>
.chat-input {
  display: flex;
  flex-direction: column;

  width: 100%;
  max-width: 48rem;

  background-color: white;
  padding: 10px;
  border-radius: 20px;
  transition: 0.3s ease-in-out;

  &:hover {
    box-shadow: 0px 13px 39px 0px rgba(0, 66, 102, 0.2);
  }

  :deep(.arco-textarea-wrapper) {
    border: none;
  }

  :deep(.arco-textarea) {
    font-size: 16px;
    background-color: white;
  }

  .actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 10px;

    .send-btn {
      color: white;
      background-color: rgb(17 25 37 / var(--tw-bg-opacity, 1));
      :deep(svg) {
        font-size: 18px;
      }
      &[disabled] {
        background-color: rgba(87, 87, 88, 0.5);
      }
    }
  }
}
</style>
