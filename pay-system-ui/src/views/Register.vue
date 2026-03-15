<template>
  <div class="page register-page">
    <div class="page-bg"></div>
    <div class="register-card">
      <div class="card-header">
        <span class="logo">💰</span>
        <h1 class="title">QG申必小金库</h1>
        <p class="subtitle">用户注册</p>
      </div>

      <form class="form" @submit.prevent="handleRegister">
        <div class="field">
          <label for="username">用户名</label>
          <input
            id="username"
            type="text"
            v-model="username"
            placeholder="请输入用户名"
            autocomplete="username"
            @keyup.enter="handleRegister"
          >
        </div>
        <div class="field">
          <label for="password">密码</label>
          <input
            id="password"
            type="password"
            v-model="password"
            placeholder="请输入密码"
            autocomplete="new-password"
            @keyup.enter="handleRegister"
          >
        </div>
        <p v-if="registerMessage" class="message" :class="{ success: registerSuccess, error: !registerSuccess }">
          {{ registerMessage }}
        </p>
        <div class="actions">
          <button type="submit" class="btn btn-primary">注 册</button>
          <button type="button" class="btn btn-ghost" @click="toLogin">已有账号？去登录</button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '../utils/request.js'

const router = useRouter()

const username = ref('')
const password = ref('')
const registerMessage = ref('')
const registerSuccess = ref(false)

const showMessage = (msg, isSuccess = false) => {
  registerMessage.value = msg
  registerSuccess.value = isSuccess
  setTimeout(() => {
    registerMessage.value = ''
  }, 3000)
}

const handleRegister = () => {
  if (!username.value) {
    showMessage('请输入用户名！', false)
    return
  }
  if (!password.value) {
    showMessage('请输入密码！', false)
    return
  }

  const registerData = {
    username: username.value,
    password: password.value,
  }

  post('/register', registerData)
    .then(() => {
      showMessage('注册成功！正在跳转…', true)
      setTimeout(() => {
        router.push('/login')
      }, 1500)
    })
    .catch((err) => {
      if (err.response) {
        showMessage('注册失败！' + (err.response.data.msg || '请稍后再试！'), false)
      } else {
        showMessage('无法连接到服务器，请检查网络设置！', false)
      }
    })
}

const toLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px;
  position: relative;
}

.page-bg {
  position: absolute;
  inset: 0;
  background: radial-gradient(ellipse 80% 50% at 50% 0%, rgba(13, 148, 136, 0.08) 0%, transparent 60%);
  pointer-events: none;
}

.register-card {
  position: relative;
  width: 100%;
  max-width: 400px;
  background: var(--color-bg-card);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-lg);
  padding: 40px 36px;
  border: 1px solid var(--color-border);
}

.card-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo {
  font-size: 48px;
  display: block;
  margin-bottom: 12px;
}

.title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-text);
  letter-spacing: -0.02em;
}

.subtitle {
  margin: 8px 0 0;
  font-size: 0.95rem;
  color: var(--color-text-muted);
  font-weight: 500;
}

.form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.field label {
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--color-text);
}

.field input {
  width: 100%;
  padding: 12px 14px;
  font-size: 1rem;
  font-family: inherit;
  color: var(--color-text);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  transition: border-color 0.2s, box-shadow 0.2s;
}

.field input::placeholder {
  color: var(--color-text-muted);
}

.field input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px var(--color-primary-light);
}

.message {
  margin: 0;
  padding: 10px 12px;
  font-size: 0.875rem;
  border-radius: var(--radius-sm);
  text-align: center;
}

.message.success {
  background: #d1fae5;
  color: #065f46;
}

.message.error {
  background: #fee2e2;
  color: #991b1b;
}

.actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 8px;
}

.btn {
  padding: 12px 20px;
  font-size: 1rem;
  font-weight: 600;
  font-family: inherit;
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.2s, color 0.2s, transform 0.1s;
  border: none;
}

.btn:active {
  transform: scale(0.98);
}

.btn-primary {
  background: var(--color-primary);
  color: #fff;
}

.btn-primary:hover {
  background: var(--color-primary-hover);
}

.btn-ghost {
  background: transparent;
  color: var(--color-text-muted);
}

.btn-ghost:hover {
  color: var(--color-primary);
  background: var(--color-primary-light);
}
</style>
