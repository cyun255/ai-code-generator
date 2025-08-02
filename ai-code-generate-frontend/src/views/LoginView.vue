<template>
  <div class="login-view">
    <a-form :model="form" :rules layout="vertical" class="login-form" @submit-success="login">
      <a-form-item class="form-title">
        <img src="/src/assets/logo.svg" alt="Logo" class="logo-img" />
        <h2>AI 应用生成平台</h2>
      </a-form-item>
      <a-form-item field="username" validate-trigger="blur" hide-asterisk>
        <a-input v-model="form.username" placeholder="用户名" />
      </a-form-item>
      <a-form-item field="password" validate-trigger="blur" hide-asterisk>
        <a-input-password v-model="form.password" placeholder="密码" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" long html-type="submit">登录</a-button>
      </a-form-item>
      <a-form-item class="register-link">
        <router-link to="/register" class="register-link">注册新用户</router-link>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useUserStore, type LoginInfo } from '@/stores/user'
import { Message } from '@arco-design/web-vue'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const form = reactive<LoginInfo>({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请正确输入用户名', minLength: 4, maxLength: 16 }],
  password: [{ required: true, message: '请正确输入密码', minLength: 8, maxLength: 32 }],
}

const login = async (values: Record<string, string>) => {
  const { username, password } = values
  try {
    await userStore.login({ username, password })
    Message.success('登录成功')
    router.replace('/')
  } catch (error) {
    Message.error(`${error}`)
  } finally {
    form.username = ''
    form.password = ''
  }
}
</script>

<style lang="scss" scoped>
.login-view {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
  min-height: calc(100vh - 161px);

  .login-form {
    width: 350px;
    padding: 24px;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .form-title {
      :deep(.arco-form-item-content-flex) {
        justify-content: center;
      }

      img {
        width: 32px;
        height: 32px;
        padding-right: 8px;
      }
    }

    .register-link {
      display: flex;
      margin-left: auto;
      color: #1890ff;
      text-decoration: none;
      margin-bottom: 0;

      &:hover {
        text-decoration: underline;
      }
    }
  }
}
</style>
