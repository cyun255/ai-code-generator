<template>
  <div class="chat-input">
    <a-textarea
      :placeholder="placeholder"
      :auto-size="{ minRows: 3, maxRows: 5 }"
      rows="3"
      @input="prompt = $event"
    />
    <div class="actions">
      <a-button
        class="send-btn"
        shape="circle"
        @click="$emit('sendMsg', prompt)"
        :disabled="!prompt.trim()"
      >
        <icon-arrow-up />
      </a-button>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { IconArrowUp } from '@arco-design/web-vue/es/icon'

defineProps({
  placeholder: {
    type: String,
    default: '请输入消息...',
  },
})

const prompt = ref('')
</script>

<style lang="scss" scoped>
.chat-input {
  display: flex;
  flex-direction: column;

  --border-radius-size: 20px;
  --background-color-inner: white;
  --background-color-outer: white;

  background-color: var(--background-color-outer);
  padding: 10px;
  border-radius: var(--border-radius-size);
  transition: 0.3s ease-in-out;

  &:hover {
    box-shadow: 0px 13px 39px 0px rgba(0, 66, 102, 0.2);
  }

  :deep(.arco-textarea-wrapper) {
    border: none;
    border-radius: var(--border-radius-size);
  }

  :deep(.arco-textarea) {
    font-size: 0.875rem;
    border-radius: var(--border-radius-size);
    background-color: var(--background-color-inner);
  }

  .actions {
    display: flex;
    margin-top: 10px;
    justify-content: flex-end;

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
