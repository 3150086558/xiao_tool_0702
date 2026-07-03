import request from '@/api/request'

// 统计汇总卡片
export function getStatsSummary(params) {
  return request({
    url: '/api/app/stats/summary',
    method: 'get',
    params
  })
}

// 分类统计列表
export function getStatsCategory(params) {
  return request({
    url: '/api/app/stats/category',
    method: 'get',
    params
  })
}

// 趋势统计
export function getStatsTrend(params) {
  return request({
    url: '/api/app/stats/trend',
    method: 'get',
    params
  })
}
