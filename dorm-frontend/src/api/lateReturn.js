import request from '@/utils/request'

export function getLateReturnList(params) {
  return request({
    url: '/api/lateReturn/page',
    method: 'get',
    params
  })
}

export function addLateReturn(data) {
  return request({
    url: '/api/lateReturn',
    method: 'post',
    data
  })
}

export function deleteLateReturn(id) {
  return request({
    url: `/api/lateReturn/${id}`,
    method: 'delete'
  })
}

export function getLeaveList(params) {
  return request({
    url: '/api/leave/page',
    method: 'get',
    params
  })
}

export function addLeave(data) {
  return request({
    url: '/api/leave',
    method: 'post',
    data
  })
}

export function updateLeave(data) {
  return request({
    url: '/api/leave',
    method: 'put',
    data
  })
}

export function deleteLeave(id) {
  return request({
    url: `/api/leave/${id}`,
    method: 'delete'
  })
}

export function approveLeave(data) {
  return request({
    url: '/api/leave/approve',
    method: 'put',
    data
  })
}
