<template>
    <div>
        <h1 id="title">QG申必小金库 - 注册</h1>
        <h2>用户注册</h2>

        <div>
            <label for="username">用户名</label>
            <input
                type="text"
                v-model="username"
                placeholder="请输入用户名"
                @keyup.enter="handleRegister"
            >
        </div>

        <div>
            <label for="password">密码</label>
            <input
                type="password"
                v-model="password"
                placeholder="请输入密码"
                @keyup.enter="handleRegister"
            >
        </div>

        <button @click="handleRegister">注 册</button>
        <button @click="toLogin">点击跳转登录</button>

        <p class="loginMessage">{{ registerMessage }}</p>
    </div>
</template>

<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {post} from '../utils/request.js'

const router = useRouter()

const username = ref('')
const password = ref('')
const registerMessage = ref('')

const showMessage = (msg) => {
    registerMessage.value = msg
    setTimeout(() => {
        registerMessage.value = ''
    }, 3000)
}

const handleRegister = () => {
    if (!username.value) {
        showMessage('请输入用户名！')
        return
    }
    if (!password.value) {
        showMessage('请输入密码！');
        return
    }

    const registerData = {
        username: username.value,
        password: password.value,
    }

    post("/register", registerData).then(res => {
        showMessage('注册成功！正在跳转！')
        setTimeout(() => {
            router.push('/login')
        }, 1500)
    }).catch(err => {
        console.log('注册失败！' + err)
        if (err.response) {
            showMessage('注册失败！' + (err.response.data.msg || '请稍后再试！'))
        } else {
            showMessage('无法连接到服务器，请检查网络设置！')
        }
    })
}

const toLogin = () => {
    router.push('/login')
}

</script>
