<template>
  <div class="main-page">
    <header class="header">
      <div class="header-inner">
        <h1 class="brand">
          <span class="brand-icon">💰</span>
          QG申必小金库
        </h1>
        <div class="user-info">
          <span class="user-name">{{ username }}</span>
          <span class="user-id">ID: {{ userId }}</span>
        </div>
      </div>
    </header>

    <main class="content">
      <aside class="sidebar">
        <div class="balance-card">
          <p class="balance-label">当前余额</p>
          <p class="balance-value">¥{{ currentBalance }}</p>
        </div>
        <nav class="nav">
          <button
            v-for="item in navItems"
            :key="item.id"
            class="nav-btn"
            :class="{ active: currentView === item.id }"
            @click="currentView = item.id"
          >
            <span class="nav-icon">{{ item.icon }}</span>
            <span class="nav-text">{{ item.label }}</span>
          </button>
        </nav>
      </aside>

      <section class="panel">
        <div v-if="message" class="toast" :class="{ success: messageSuccess, error: !messageSuccess }">
          {{ message }}
        </div>

        <!-- 存款 -->
        <div v-if="currentView === 'deposit'" class="card form-card">
          <h2 class="card-title">存款</h2>
          <p class="card-desc">上交保护费预备</p>
          <div class="form-row">
            <input
              id="deposit"
              v-model.number="deposit.amount"
              type="number"
              placeholder="存款金额"
              min="0"
              step="0.01"
              class="input"
            >
            <button class="btn btn-primary" @click="handleDeposit">确认存款</button>
          </div>
        </div>

        <!-- 取款 -->
        <div v-if="currentView === 'withdraw'" class="card form-card">
          <h2 class="card-title">取款</h2>
          <p class="card-desc">卷钱跑路</p>
          <div class="form-row">
            <input
              id="withdraw"
              v-model.number="withdraw.amount"
              type="number"
              placeholder="取款金额"
              min="0"
              step="0.01"
              class="input"
            >
            <button class="btn btn-primary" @click="handleWithdraw">确认取款</button>
          </div>
        </div>

        <!-- 转账 -->
        <div v-if="currentView === 'transfer'" class="card form-card">
          <h2 class="card-title">转账</h2>
          <p class="card-desc">上交保护费</p>
          <div class="form-fields">
            <input
              id="transfer-user"
              v-model.number="transfer.targetUserId"
              type="text"
              placeholder="对方账号 ID"
              class="input"
            >
            <input
              v-model.number="transfer.amount"
              type="number"
              placeholder="转账金额"
              min="0"
              step="0.01"
              class="input"
            >
            <button class="btn btn-primary" @click="handleTransfer">确认转账</button>
          </div>
        </div>

        <!-- 交易流水 -->
        <div v-if="currentView === 'transactions'" class="card table-card">
          <div class="card-head">
            <h2 class="card-title">交易流水</h2>
            <button class="btn btn-secondary" @click="handleGetTransactions">查询</button>
          </div>
          <div class="table-wrap">
            <table class="table">
              <thead>
                <tr>
                  <th>时间</th>
                  <th>类型</th>
                  <th>金额</th>
                  <th>余额</th>
                  <th>对方 ID</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="tx in transactions" :key="tx.id">
                  <td>{{ tx.createTime }}</td>
                  <td>
                    <span class="type-badge" :class="'type-' + tx.type">{{ getTypeText(tx.type) }}</span>
                  </td>
                  <td class="amount">{{ tx.amount.toFixed(2) }}</td>
                  <td>{{ tx.newBalance.toFixed(2) }}</td>
                  <td>{{ tx.targetUserId || '—' }}</td>
                </tr>
                <tr v-if="transactions.length === 0">
                  <td colspan="5" class="empty">暂无记录，点击「查询」获取</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { post } from '@/utils/request.js'

const router = useRouter()

const isLoggedIn = sessionStorage.getItem('isLoggedIn')
if (!isLoggedIn || isLoggedIn !== 'true') {
  router.push('/login')
}

const navItems = [
  { id: 'deposit', label: '存款', icon: '📥' },
  { id: 'withdraw', label: '取款', icon: '📤' },
  { id: 'transfer', label: '转账', icon: '↔️' },
  { id: 'transactions', label: '交易流水', icon: '📋' },
]

const currentView = ref('deposit')
const username = ref('')
const userId = ref('')
const currentBalance = ref(0)
const message = ref('')
const messageSuccess = ref(true)

const fetchUserInfo = async () => {
  const id = sessionStorage.getItem('userId')
  if (!id) return
  try {
    const res = await post('/userInfo', { userId: id })
    username.value = res.data.username
    userId.value = res.data.id
    currentBalance.value = res.data.balance
  } catch (err) {
    console.error('用户信息获取失败', err)
  }
}

onMounted(() => {
  fetchUserInfo()
})

const deposit = ref({ amount: '' })
const withdraw = ref({ amount: '' })
const transfer = ref({ targetUserId: '', amount: '' })
const transactions = ref([])

const showMessage = (msg, isSuccess = true) => {
  message.value = msg
  messageSuccess.value = isSuccess
  setTimeout(() => {
    message.value = ''
  }, 3000)
}

const getTypeText = (type) => {
  const map = { 1: '存款', 2: '取款', 3: '转入', 4: '转出' }
  return map[type] || '其他'
}

const handleDeposit = async () => {
  if (deposit.value.amount === '' || deposit.value.amount == null) {
    showMessage('金额不能为空！', false)
    return
  }
  const payload = { userId: userId.value, amount: deposit.value.amount }
  post('/deposit', payload)
    .then((res) => {
      showMessage('存款成功！')
      currentBalance.value = res.data
    })
    .catch((err) => {
      const msg = err.response?.data?.msg || '请稍后再试！'
      showMessage('存款失败！' + msg, false)
    })
}

const handleWithdraw = async () => {
  if (withdraw.value.amount === '' || withdraw.value.amount == null) {
    showMessage('金额不能为空！', false)
    return
  }
  const payload = { userId: userId.value, amount: withdraw.value.amount }
  post('/withdraw', payload)
    .then((res) => {
      showMessage('取款成功！')
      currentBalance.value = res.data
    })
    .catch((err) => {
      const msg = err.response?.data?.msg || '请稍后再试！'
      showMessage('取款失败！' + msg, false)
    })
}

const handleTransfer = async () => {
  if (!transfer.value.targetUserId) {
    showMessage('转账用户 ID 不能为空！', false)
    return
  }
  if (transfer.value.amount === '' || transfer.value.amount == null) {
    showMessage('金额不能为空！', false)
    return
  }
  const payload = {
    userId: userId.value,
    targetUserId: transfer.value.targetUserId,
    amount: transfer.value.amount,
  }
  post('/transfer', payload)
    .then((res) => {
      showMessage('转账成功！')
      currentBalance.value = res.data
    })
    .catch((err) => {
      const msg = err.response?.data?.msg || '请稍后再试！'
      showMessage('转账失败！' + msg, false)
    })
}

const handleGetTransactions = async () => {
  const payload = { userId: userId.value }
  post('/getTransactions', payload)
    .then((res) => {
      transactions.value = res.data || []
      if (transactions.value.length === 0) showMessage('暂无交易记录')
    })
    .catch((err) => {
      const msg = err.response?.data?.msg || '请稍后再试！'
      showMessage('查询失败！' + msg, false)
    })
}
</script>

<style scoped>
.main-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  background: var(--color-bg-card);
  border-bottom: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.brand {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 700;
  color: var(--color-text);
  display: flex;
  align-items: center;
  gap: 8px;
}

.brand-icon {
  font-size: 1.5rem;
}

.user-info {
  display: flex;
  align-items: baseline;
  gap: 12px;
  font-size: 0.875rem;
}

.user-name {
  font-weight: 600;
  color: var(--color-text);
}

.user-id {
  color: var(--color-text-muted);
}

.content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
  padding: 24px;
  display: flex;
  gap: 24px;
}

.sidebar {
  flex-shrink: 0;
  width: 220px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.balance-card {
  background: linear-gradient(135deg, var(--color-primary) 0%, var(--color-primary-hover) 100%);
  color: #fff;
  padding: 20px;
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-md);
}

.balance-label {
  margin: 0;
  font-size: 0.875rem;
  opacity: 0.9;
}

.balance-value {
  margin: 8px 0 0;
  font-size: 1.75rem;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.nav {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.nav-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 14px;
  font-size: 0.95rem;
  font-family: inherit;
  font-weight: 500;
  color: var(--color-text);
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: background 0.2s, border-color 0.2s, color 0.2s;
  text-align: left;
}

.nav-btn:hover {
  background: var(--color-bg);
  border-color: var(--color-primary);
  color: var(--color-primary);
}

.nav-btn.active {
  background: var(--color-primary-light);
  border-color: var(--color-primary);
  color: var(--color-primary-hover);
}

.nav-icon {
  font-size: 1.1rem;
}

.panel {
  flex: 1;
  min-width: 0;
  position: relative;
}

.toast {
  position: absolute;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  padding: 10px 20px;
  border-radius: var(--radius-md);
  font-size: 0.875rem;
  font-weight: 500;
  z-index: 10;
  animation: fadeIn 0.2s ease;
}

.toast.success {
  background: #d1fae5;
  color: #065f46;
}

.toast.error {
  background: #fee2e2;
  color: #991b1b;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateX(-50%) translateY(-8px); }
  to { opacity: 1; transform: translateX(-50%) translateY(0); }
}

.card {
  background: var(--color-bg-card);
  border-radius: var(--radius-lg);
  padding: 24px;
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
}

.card-title {
  margin: 0 0 4px;
  font-size: 1.15rem;
  font-weight: 600;
  color: var(--color-text);
}

.card-desc {
  margin: 0 0 20px;
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.form-card .form-row,
.form-card .form-fields {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.input {
  padding: 10px 14px;
  font-size: 1rem;
  font-family: inherit;
  color: var(--color-text);
  background: var(--color-bg);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  min-width: 160px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 2px var(--color-primary-light);
}

.btn {
  padding: 10px 18px;
  font-size: 0.95rem;
  font-weight: 600;
  font-family: inherit;
  border-radius: var(--radius-md);
  cursor: pointer;
  border: none;
  transition: background 0.2s, color 0.2s, transform 0.1s;
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

.btn-secondary {
  background: var(--color-bg);
  color: var(--color-text);
  border: 1px solid var(--color-border);
}

.btn-secondary:hover {
  background: var(--color-border);
}

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.card-head .card-title {
  margin: 0;
}

.table-card {
  overflow: hidden;
}

.table-wrap {
  overflow-x: auto;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
}

.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 0.9rem;
}

.table th,
.table td {
  padding: 12px 14px;
  text-align: left;
  border-bottom: 1px solid var(--color-border);
}

.table th {
  background: var(--color-bg);
  font-weight: 600;
  color: var(--color-text);
}

.table tbody tr:last-child td {
  border-bottom: none;
}

.table tbody tr:hover {
  background: var(--color-bg);
}

.table .amount {
  font-weight: 500;
  font-variant-numeric: tabular-nums;
}

.type-badge {
  display: inline-block;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
}

.type-1 { background: #d1fae5; color: #065f46; }
.type-2 { background: #fee2e2; color: #991b1b; }
.type-3 { background: #dbeafe; color: #1e40af; }
.type-4 { background: #fef3c7; color: #92400e; }

.empty {
  color: var(--color-text-muted);
  text-align: center;
  padding: 32px !important;
}

@media (max-width: 768px) {
  .content {
    flex-direction: column;
  }
  .sidebar {
    width: 100%;
    flex-direction: row;
    flex-wrap: wrap;
  }
  .balance-card {
    flex: 1;
    min-width: 160px;
  }
  .nav {
    flex-direction: row;
    flex-wrap: wrap;
    flex: 1;
  }
  .nav-btn {
    flex: 1;
    min-width: 120px;
  }
}
</style>
