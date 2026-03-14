<template>
    <div>
        <h1>QG申必小金库 - 登录</h1>
        <h2>用户登录</h2>

        <div>
            <label for="username">用户名</label>
            <input
                type="text"
                v-model="username"
                placeholder="请输入用户名"
                @keyup.enter="handleLogin"
            >
        </div>

        <div>
            <label for="password">密码</label>
            <input
                type="password"
                v-model="password"
                placeholder="请输入密码"
                @keyup.enter="handleLogin"
            >
        </div>

        <button @click="handleLogin">登 录</button>
        <button @click="toRegister">点击跳转注册</button>

        <p class="loginMessage">{{ loginMessage }}</p>
    </div>
</template>

<script setup>
import {ref} from 'vue'
import {useRouter} from 'vue-router'
import {post} from '../utils/request.js'

const router = useRouter()

const username = ref('')
const password = ref('')
const loginMessage = ref('')
const loginSuccess = ref('')

const showMessage = (msg, isSuccess) => {
    loginMessage.value = msg
    loginSuccess.value = isSuccess
    setTimeout(() => {
        loginMessage.value = ''
    }, 3000)
}

const handleLogin = () => {
    if (!username.value) {
        showMessage('请输入用户名！', false)
        return
    }
    if (!password.value) {
        showMessage('请输入密码！', false);
        return
    }

    const loginData = {
        username: username.value,
        password: password.value,
    }

    post("/login", loginData).then(res => {
        showMessage('登录成功！正在跳转！', true)
        // 保存用户信息
        const username = res.data.username;
        const userId = res.data.id;
        const balance = res.data.balance;
        sessionStorage.setItem('isLoggedIn', 'true');
        sessionStorage.setItem('username', username);
        sessionStorage.setItem('userId', userId);
        sessionStorage.setItem('balance', balance);
        setTimeout(() => {
            router.push('/main')
        }, 1500)
    }).catch(err => {
        console.log('登录失败！' + err)
        if (err.response) {
            showMessage('登录失败！' + (err.response.data.msg || '请稍后再试！'), false)
        } else {
            showMessage('无法连接到服务器，请检查网络设置！', false)
        }
    })
}

const toRegister = () => {
    router.push('/register')
}

</script>
