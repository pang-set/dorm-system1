import request from '@/utils/request'

export function getViolationList(params) {
  return request({
    url: '/api/violation/page',
    method: 'get',
    params
  })
}

export function getViolation(id) {
  return request({
    url: `/api/violation/${id}`,
    method: 'get'
  })
}

export function addViolation(data) {
  return request({
    url: '/api/violation',
    method: 'post',
    data
  })
}

export function updateViolation(data) {
  return request({
    url: '/api/violation',
    method: 'put',
    data
  })
}

export function deleteViolation(id) {
  return request({
    url: `/api/violation/${id}`,
    method: 'delete'
  })
}

export function handleViolation(id, data) {
  return request({
    url: `/api/violation/handle/${id}`,
    method: 'put',
    data
  })
}
