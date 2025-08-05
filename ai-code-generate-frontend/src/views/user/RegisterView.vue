<template>
  <div class="register-view">
    <a-form
      :model="form"
      :rules="rules"
      layout="vertical"
      class="register-form"
      @submit-success="register"
    >
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
      <a-form-item field="confirmPassword" validate-trigger="blur" hide-asterisk>
        <a-input-password v-model="form.confirmPassword" placeholder="确认密码" />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" long html-type="submit">注册</a-button>
      </a-form-item>
      <a-form-item class="login-link">
        <router-link to="/login" class="login-link">已有账号？立即登录</router-link>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useUserStore, type RegisterInfo } from '@/stores/user'
import { Message } from '@arco-design/web-vue'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()

const form = reactive<RegisterInfo & { confirmPassword: string }>({
  username: '',
  password: '',
  confirmPassword: '',
})

const validateConfirmPassword = (value: string, cb: (error?: string) => void) => {
  if (value !== form.password) {
    cb('两次输入的密码不一致')
  } else {
    cb()
  }
}

const rules = {
  username: [{ required: true, message: '请正确输入用户名', minLength: 4, maxLength: 16 }],
  password: [{ required: true, message: '请正确输入密码', minLength: 8, maxLength: 32 }],
  confirmPassword: [
    { required: true, message: '请确认密码' },
    { validator: validateConfirmPassword },
  ],
}

const register = async (values: Record<string, string>) => {
  const { username, password } = values
  try {
    await userStore.register({ username, password })
    Message.success('注册成功')
    router.replace('/login')
  } catch (error) {
    Message.error(`${error}`)
  } finally {
    form.username = ''
    form.password = ''
    form.confirmPassword = ''
  }
}
</script>

<style lang="scss" scoped>
.register-view {
  display: flex;
  justify-content: center;
  align-items: center;
  background: #fff;
  min-height: calc(100vh - 161px);

  .register-form {
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

    .login-link {
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
