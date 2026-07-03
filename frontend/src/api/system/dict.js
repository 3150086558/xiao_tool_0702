import request from '@/api/request'

export function getDictTypePage(params) {
  return request({
    url: '/api/sys/dict/type/page',
    method: 'get',
    params
  })
}

export function getDictTypeAll() {
  return request({
    url: '/api/sys/dict/type/all',
    method: 'get'
  })
}

export function createDictType(data) {
  return request({
    url: '/api/sys/dict/type',
    method: 'post',
    data
  })
}

export function updateDictType(id, data) {
  return request({
    url: '/api/sys/dict/type/' + id,
    method: 'put',
    data
  })
}

export function deleteDictType(id) {
  return request({
    url: '/api/sys/dict/type/' + id,
    method: 'delete'
  })
}

export function getDictDataPage(params) {
  return request({
    url: '/api/sys/dict/data/page',
    method: 'get',
    params
  })
}

export function getDictDataByCode(dictCode) {
  return request({
    url: '/api/sys/dict/data/code/' + dictCode,
    method: 'get'
  })
}

export function createDictData(data) {
  return request({
    url: '/api/sys/dict/data',
    method: 'post',
    data
  })
}

export function updateDictData(id, data) {
  return request({
    url: '/api/sys/dict/data/' + id,
    method: 'put',
    data
  })
}

export function deleteDictData(id) {
  return request({
    url: '/api/sys/dict/data/' + id,
    method: 'delete'
  })
}
