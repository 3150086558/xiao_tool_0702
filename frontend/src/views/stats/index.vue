<template>
  <div class="app-container">
    <el-card shadow="never" class="filter-container">
      <el-form :inline="true" :model="query" class="search-form">
        <el-form-item label="月份">
          <el-date-picker
            v-model="query.month"
            type="month"
            placeholder="选择月份"
            value-format="YYYY-MM"
            style="width: 160px"
          />
        </el-form-item>
        <el-form-item label="收支类型">
          <el-radio-group v-model="query.type" size="default" @change="handleTypeChange">
            <el-radio-button value="expense">支出</el-radio-button>
            <el-radio-button value="income">收入</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" v-loading="summaryLoading">
      <el-col :xs="24" :sm="12" :md="6" v-for="card in summaryCards" :key="card.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card-body">
            <div class="stat-icon" :style="{ background: card.color }">
              <el-icon><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-title">{{ card.title }}</div>
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-sub">{{ card.sub }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <template #header>
        <div class="card-header">
          <span>分类统计</span>
          <el-radio-group v-model="query.type" size="small" @change="handleSearch">
            <el-radio-button value="expense">支出</el-radio-button>
            <el-radio-button value="income">收入</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table v-loading="categoryLoading" :data="categoryData" border stripe>
        <el-table-column type="index" label="排名" width="80" align="center" />
        <el-table-column prop="category" label="分类" min-width="140" show-overflow-tooltip />
        <el-table-column prop="amount" label="金额" width="140" align="right">
          <template #default="{ row }">
            <span class="text-money">¥ {{ formatMoney(row.amount) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="count" label="笔数" width="100" align="center" />
        <el-table-column label="占比" min-width="240">
          <template #default="{ row }">
            <el-progress :percentage="row.percent" :color="query.type === 'income' ? '#67c23a' : '#409eff'" />
          </template>
        </el-table-column>
        <el-table-column prop="avgAmount" label="平均金额" width="140" align="right">
          <template #default="{ row }">
            ¥ {{ formatMoney(row.avgAmount) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header><span>分类占比</span></template>
          <div class="category-bars">
            <div v-for="item in categoryData.slice(0, 6)" :key="item.category" class="bar-item">
              <span class="bar-label">{{ item.category }}</span>
              <div class="bar-track">
                <div class="bar-fill" :style="{ width: item.percent + '%', background: barColor(item.percent) }"></div>
              </div>
              <span class="bar-value">{{ item.percent }}%</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>
            <div class="card-header">
              <span>近期明细</span>
              <span v-if="!recentRealData" class="mock-tip">（示例）</span>
            </div>
          </template>
          <el-table v-loading="recentLoading" :data="recentData" border size="small">
            <el-table-column prop="date" label="日期" width="110" />
            <el-table-column prop="item" label="项目" min-width="120" show-overflow-tooltip />
            <el-table-column prop="category" label="分类" width="90" align="center" />
            <el-table-column prop="amount" label="金额" width="100" align="right">
              <template #default="{ row }">
                <span :class="row.type === 'income' ? 'text-income' : 'text-expense'">
                  ¥ {{ formatMoney(row.amount) }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getStatsSummary, getStatsCategory } from '@/api/app/stats'
import { getAccountingPage } from '@/api/app/accounting'

function getCurrentMonth() {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  return `${year}-${month}`
}

const summaryLoading = ref(false)
const categoryLoading = ref(false)
const recentLoading = ref(false)
const summaryCards = ref([])
const categoryData = ref([])
const recentData = ref([])
const recentRealData = ref(false)

const query = reactive({
  month: getCurrentMonth(),
  type: 'expense'
})

async function loadSummary() {
  summaryLoading.value = true
  try {
    const res = await getStatsSummary({ month: query.month })
    const d = res.data || {}
    const income = d.income || 0
    const expense = d.expense || 0
    const balance = d.balance || (income - expense)
    const categories = d.categories || []
    const totalCount = categories.reduce((s, c) => s + (c.count || 0), 0)
    summaryCards.value = [
      { title: '本月收入', value: '¥ ' + formatMoney(income), sub: '', icon: 'Top', color: '#67c23a' },
      { title: '本月支出', value: '¥ ' + formatMoney(expense), sub: '', icon: 'Bottom', color: '#f56c6c' },
      { title: '本月结余', value: '¥ ' + formatMoney(balance), sub: '', icon: 'Wallet', color: '#409eff' },
      { title: '记账笔数', value: totalCount || '0', sub: '', icon: 'Document', color: '#e6a23c' }
    ]
  } catch (e) {
    summaryCards.value = [
      { title: '本月收入', value: '¥ 0.00', sub: '', icon: 'Top', color: '#67c23a' },
      { title: '本月支出', value: '¥ 0.00', sub: '', icon: 'Bottom', color: '#f56c6c' },
      { title: '本月结余', value: '¥ 0.00', sub: '', icon: 'Wallet', color: '#409eff' },
      { title: '记账笔数', value: '0', sub: '', icon: 'Document', color: '#e6a23c' }
    ]
  } finally {
    summaryLoading.value = false
  }
}

async function loadCategory() {
  categoryLoading.value = true
  try {
    const res = await getStatsCategory({ month: query.month, type: query.type })
    const raw = res.data || []
    const totalAmount = raw.reduce((s, c) => s + (c.amount || 0), 0)
    const totalCount = raw.reduce((s, c) => s + (c.count || 0), 0)
    categoryData.value = raw.map(c => ({
      category: c.category,
      amount: c.amount || 0,
      count: c.count || 0,
      percent: totalAmount > 0 ? Math.round((c.amount || 0) / totalAmount * 100) : 0,
      avgAmount: (c.count || 0) > 0 ? Number(((c.amount || 0) / (c.count || 1)).toFixed(2)) : 0
    }))
  } catch (e) {
    categoryData.value = []
  } finally {
    categoryLoading.value = false
  }
}

async function loadRecent() {
  recentLoading.value = true
  try {
    const month = query.month || ''
    const startDate = month ? `${month}-01` : ''
    const endDate = month ? `${month}-31` : ''
    const res = await getAccountingPage({ page: 1, size: 5, startDate, endDate })
    const records = res.data?.records || []
    if (records.length > 0) {
      recentRealData.value = true
      recentData.value = records.map(row => ({
        date: row.record_date || row.date || '',
        item: row.category || row.item || '',
        category: row.sub_category || row.category || '',
        amount: Number(row.amount || 0),
        type: row.type || 'expense'
      }))
    } else {
      loadMockRecent()
    }
  } catch (e) {
    loadMockRecent()
  } finally {
    recentLoading.value = false
  }
}

function loadMockRecent() {
  recentRealData.value = false
  recentData.value = [
    { date: '2026-06-28', item: '早餐', category: '餐饮', amount: 18, type: 'expense' },
    { date: '2026-06-27', item: '打车', category: '交通', amount: 32, type: 'expense' },
    { date: '2026-06-26', item: '超市采购', category: '购物', amount: 256, type: 'expense' },
    { date: '2026-06-25', item: '电影票', category: '娱乐', amount: 80, type: 'expense' },
    { date: '2026-06-24', item: '房租', category: '住房', amount: 1500, type: 'expense' }
  ]
}

function formatMoney(val) {
  return Number(val || 0).toFixed(2)
}

function barColor(percent) {
  if (percent > 25) return '#f56c6c'
  if (percent > 15) return '#e6a23c'
  return '#409eff'
}

function handleSearch() {
  loadSummary()
  loadCategory()
  loadRecent()
}

function handleTypeChange() {
  handleSearch()
}

function handleReset() {
  query.month = getCurrentMonth()
  query.type = 'expense'
  handleSearch()
}

onMounted(() => {
  loadSummary()
  loadCategory()
  loadRecent()
})
</script>

<style scoped>
.filter-container {
  margin-bottom: 16px;
}

.search-form {
  margin: 0;
}

.stat-card {
  border-radius: 8px;
  margin-bottom: 16px;
}

.stat-card-body {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 26px;
}

.stat-title {
  font-size: 13px;
  color: #909399;
}

.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: #303133;
  margin: 2px 0;
}

.stat-sub {
  font-size: 12px;
  color: #c0c4cc;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mock-tip {
  font-size: 12px;
  color: #909399;
  font-weight: normal;
}

.text-money {
  color: #f56c6c;
  font-weight: 600;
}

.text-income {
  color: #67c23a;
  font-weight: 600;
}

.text-expense {
  color: #f56c6c;
  font-weight: 600;
}

.category-bars {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.bar-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bar-label {
  width: 60px;
  font-size: 13px;
  color: #606266;
}

.bar-track {
  flex: 1;
  height: 16px;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 8px;
  transition: width 0.4s;
}

.bar-value {
  width: 50px;
  text-align: right;
  font-size: 12px;
  color: #909399;
}
</style>
