import request from '@/utils/request'

export function getRepairList(params) {
  return request({
    url: '/api/repair/page',
    method: 'get',
    params
  })
}

export function getRepair(id) {
  return request({
    url: `/api/repair/${id}`,
    method: 'get'
  })
}

export function addRepair(data) {
  return request({
    url: '/api/repair',
    method: 'post',
    data
  })
}

export function updateRepair(data) {
  return request({
    url: '/api/repair',
    method: 'put',
    data
  })
}

export function deleteRepair(id) {
  return request({
    url: `/api/repair/${id}`,
    method: 'delete'
  })
}

export function assignRepair(id, data) {
  return request({
    url: `/api/repair/assign/${id}`,
    method: 'put',
    data
  })
}

export function completeRepair(id, data) {
  return request({
    url: `/api/repair/complete/${id}`,
    method: 'put',
    data
  })
}

export function rateRepair(id, data) {
  return request({
    url: `/api/repair/rate/${id}`,
    method: 'put',
    data
  })
}
