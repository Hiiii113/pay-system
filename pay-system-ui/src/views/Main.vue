<template>
    <div id="app">
        <h2>💰QG申必小金库💰</h2>

        <!-- 用户信息 -->
        <p>当前用户：{{ username }}（id：{{ userId }}）</p>
        <p>余额：¥{{ currentBalance }}</p>

        <!-- 功能菜单 -->
        <ul>
            <li @click="currentView = 'deposit'">存款（上交保护费预备）</li>
            <li @click="currentView = 'withdraw'">取款（卷钱跑路）</li>
            <li @click="currentView = 'transfer'">转账（上交保护费）</li>
            <li @click="currentView = 'transactions'">查看交易流水</li>
        </ul>

        <!-- 存款表单容器 -->
        <div v-if="currentView === 'deposit'">
            <label for="deposit">存款</label>
            <input id="deposit" v-model.number="deposit.amount" type="number"
                   placeholder="存款金额">
            <button @click="handleDeposit">确认存款</button>
        </div>

        <!-- 取款表单容器 -->
        <div v-if="currentView === 'withdraw'">
            <label for="withdraw">取款</label>
            <input id="withdraw" v-model.number="withdraw.amount" type="number"
                   placeholder="取款金额">
            <button @click="handleWithdraw">确认取款</button>
        </div>

        <!-- 转账表单容器 -->
        <div v-if="currentView === 'transfer'">
            <label for="transfer">转账</label>
            <input id="transfer" v-model.number="transfer.targetUserId" placeholder="对方账号">
            <input v-model.number="transfer.amount" type="number" placeholder="转账金额">
            <button @click="handleTransfer">确认转账</button>
        </div>

        <!-- 交易流水容器 -->
        <div v-if="currentView === 'transactions'">
            <h3>交易流水</h3>
            <button @click="handleGetTransactions">查 询</button>
            <table>
                <thead>
                <tr>
                    <th>时间</th>
                    <th>类型</th>
                    <th>金额</th>
                    <th>余额</th>
                    <th>对方id</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="tx in transactions" :key="tx.id">
                    <td>{{ tx.createTime }}</td>
                    <td>{{ getTypeText(tx.type) }}</td>
                    <td>
                        {{ tx.amount.toFixed(2) }}
                    </td>
                    <td>{{ tx.newBalance.toFixed(2) }}</td>
                    <td>{{ tx.targetUserId || '-' }}</td>
                </tr>
                </tbody>
            </table>
        </div>

        <div v-if="message">
            <!--提示信息-->
            {{ message }}
        </div>
    </div>
</template>

<script setup>
import {onMounted, ref} from 'vue'
import {useRouter} from 'vue-router'
import {post} from "@/utils/request.js";

const router = useRouter()

// 先看有没有登录
const isLoggedIn = sessionStorage.getItem('isLoggedIn');
if (!isLoggedIn || isLoggedIn !== 'true') {
    alert('请先登录！');
    router.push('/login');
}

// 定义变量
const currentView = ref('deposit')
const username = ref('')
const userId = ref('')
const currentBalance = ref(0)
const message = ref('')

// 加载用户信息
const fetchUserInfo = async () => {
    // 从读取ID
    const id = sessionStorage.getItem('userId')
    if (!id) return;

    try {
        const res = await post("/userInfo", {userId: id})
        username.value = res.data.username
        userId.value = res.data.id
        currentBalance.value = res.data.balance
    } catch (err) {
        console.error('用户信息获取失败', err)
    }
}

// onMounted
onMounted(() => {
    fetchUserInfo()
})

// 定义表单对象（需要提交的）
const deposit = ref({amount: '0'})
const withdraw = ref({amount: '0'})
const transfer = ref({targetUserId: '', amount: '0'})
let transactions = ref([])

const showMessage = (msg) => {
    message.value = msg
    setTimeout(() => {
        message.value = ''
    }, 3000)
}

const getTypeText = (type) => {
    switch (type) {
        case 1:
            return '存款'
        case 2:
            return '取款'
        case 3:
            return '转入'
        case 4:
            return '转出'
        default:
            return '其他'
    }
}

const handleDeposit = async () => {
    // 检验是否为空
    if (!deposit.value.amount) {
        alert('金额不能为空！');
        return;
    }
    // 组装
    const payload = {userId: userId.value, amount: deposit.value.amount};
    // 开始请求
    post("/deposit", payload).then(res => {
        showMessage('存款成功！')
        // 更新余额
        currentBalance.value = res.data
    }).catch(err => {
        console.log('存款失败！' + err)
        if (err.response) {
            showMessage('存款失败！' + (err.response.data.msg || '请稍后再试！'), false)
        } else {
            showMessage('无法连接到服务器，请检查网络设置！', false)
        }
    })
}

const handleWithdraw = async () => {
    // 检验是否为空
    if (!withdraw.value.amount) {
        alert('金额不能为空！');
        return;
    }
    // 组装
    const payload = {userId: userId.value, amount: withdraw.value.amount};
    // 开始请求
    post("/withdraw", payload).then(res => {
        showMessage('取款成功！')
        // 更新余额
        currentBalance.value = res.data
    }).catch(err => {
        console.log('取款失败！' + err)
        if (err.response) {
            showMessage('取款失败！' + (err.response.data.msg || '请稍后再试！'), false)
        } else {
            showMessage('无法连接到服务器，请检查网络设置！', false)
        }
    })
}

const handleTransfer = async () => {
    // 检验是否为空
    if (!transfer.value.amount) {
        showMessage('金额不能为空！');
        return;
    }
    if (!transfer.value.targetUserId) {
        showMessage('转账用户id不能为空！');
        return;
    }

    // 组装
    const payload = {
        userId: userId.value,
        targetUserId: transfer.value.targetUserId,
        amount: transfer.value.amount
    };
    // 开始请求
    post("/transfer", payload).then(res => {
        showMessage('转账成功！')
        // 更新余额
        currentBalance.value = res.data
    }).catch(err => {
        console.log('转账失败！' + err)
        if (err.response) {
            showMessage('转账失败！' + (err.response.data.msg || '请稍后再试！'), false)
        } else {
            showMessage('无法连接到服务器，请检查网络设置！', false)
        }
    })
}

const handleGetTransactions = async () => {
    // 组装
    const payload = {userId: userId.value};
    // 开始请求
    post("/getTransactions", payload).then(res => {
        // 更新表格
        transactions.value = res.data;
    }).catch(err => {
        console.log('查询失败失败！' + err)
        if (err.response) {
            showMessage('查询失败！' + (err.response.data.msg || '请稍后再试！'), false)
        } else {
            showMessage('无法连接到服务器，请检查网络设置！', false)
        }
    })
}

</script>

<style scoped>

table {
    border-collapse: collapse;
    width: 70%;
    margin: 20px 0;
}

th, td {
    border: 1px solid black;
    padding: 12px;
    text-align: left;
}

th {
    background-color: #CDCDCD;
    font-weight: bold;
}

</style>
